(function () {
    const config = window.eventsPageConfig || {};
    const rawUserId = config.currentUserId;
    const currentUserId = typeof rawUserId === 'number' ? rawUserId : (rawUserId ? Number(rawUserId) : null);
    const currentUserRole = config.currentUserRole && config.currentUserRole !== 'null' ? config.currentUserRole : null;
    const eventTypes = Array.isArray(config.eventTypes) ? config.eventTypes : [];
    const eventStates = Array.isArray(config.eventStates) ? config.eventStates : [];

    const eventsContainer = document.getElementById('events-container');
    const feedbackEl = document.getElementById('event-feedback');
    const createEventForm = document.getElementById('create-event-form');
    const createEventSection = document.getElementById('create-event-section');
    const createEventToggleBtn = document.getElementById('toggle-create-event');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
    const API_BASE = '/events/api';
    const sellerRoles = ['PRODUCER', 'DISTRIBUTOR', 'TRANSFORMER'];
    let eventsCache = [];

    function findEvent(eventId) {
        return eventsCache.find((event) => String(event.id) === String(eventId));
    }

    function clearFeedback() {
        if (!feedbackEl) {
            return;
        }
        feedbackEl.hidden = true;
        feedbackEl.textContent = '';
        feedbackEl.classList.remove('alert--success', 'alert--error');
    }

    function showFeedback(message, type = 'success') {
        if (!feedbackEl) {
            return;
        }
        feedbackEl.textContent = message;
        feedbackEl.hidden = false;
        feedbackEl.classList.remove('alert--success', 'alert--error');
        feedbackEl.classList.add(type === 'error' ? 'alert--error' : 'alert--success');
    }

    async function extractError(response) {
        try {
            const text = await response.text();
            if (!text) {
                return `Errore ${response.status}`;
            }
            return text;
        } catch (error) {
            return `Errore ${response.status}`;
        }
    }

    async function sendRequest(url, method, payload) {
        const headers = { 'Accept': 'application/json' };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        const options = { method, headers };
        if (payload !== undefined) {
            headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(payload);
        }
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error(await extractError(response));
        }
        if (response.status === 204) {
            return null;
        }
        const contentType = response.headers.get('Content-Type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return null;
    }

    function formatDateTime(value) {
        if (!value) {
            return 'N/D';
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return value;
        }
        return date.toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatCurrency(value, currencyCode = 'EUR') {
        if (value === null || value === undefined) {
            return 'N/D';
        }
        const numeric = Number(value);
        if (Number.isFinite(numeric)) {
            const safeCurrency = (currencyCode || 'EUR').toUpperCase();
            return new Intl.NumberFormat('it-IT', { style: 'currency', currency: safeCurrency }).format(numeric);        }
        return value;
    }

    function getBadgeClass(state) {
        switch (state) {
            case 'PUBLISHED':
                return 'badge--published';
            case 'CANCELLED':
                return 'badge--cancelled';
            case 'ARCHIVED':
                return 'badge--archived';
            case 'DRAFT':
            default:
                return 'badge--draft';
        }
    }

    function createMetaRow(label, value) {
        const row = document.createElement('div');
        row.className = 'event-card__meta-row';
        const labelEl = document.createElement('span');
        labelEl.textContent = label;
        const valueEl = document.createElement('span');
        valueEl.textContent = value ?? 'N/D';
        row.append(labelEl, valueEl);
        return row;
    }

    function createListBlock(title, values) {
        const container = document.createElement('div');

        const heading = document.createElement('h5');
        heading.className = 'event-card__section-title';
        heading.textContent = title;

        const content = document.createElement('p');
        content.className = 'event-card__list-text';

        if (Array.isArray(values) && values.length > 0) {
            content.textContent = values.join(', ');
        } else {
            content.textContent = 'Nessuno';
        }

        container.append(heading, content);
        return container;
    }

    function extractLocationAddress(location) {
        if (!location) {
            return '';
        }
        if (typeof location === 'string') {
            return location;
        }
        if (typeof location === 'object' && 'address' in location) {
            return location.address || '';
        }
        return '';
    }

    function createParticipantsSection(event) {
        const details = document.createElement('details');
        details.className = 'event-card__section';

        const summary = document.createElement('summary');
        summary.textContent = 'Dettagli partecipanti';
        details.appendChild(summary);

        const wrapper = document.createElement('div');
        wrapper.className = 'event-card__participants';

        const confirmedParticipants = Array.from(new Set([
            ...(event.buyerEmails ?? []),
            ...(event.confirmedSellerEmails ?? [])
        ]));

        const invitedParticipants = Array.from(new Set([
            ...(event.invitedSellerEmails ?? [])
        ]));

        wrapper.appendChild(createListBlock('Partecipanti', confirmedParticipants));

        if (invitedParticipants.length > 0) {
            wrapper.appendChild(createListBlock('Invitati (in attesa di conferma)', invitedParticipants));
        }

        details.appendChild(wrapper);
        return details;
    }



    function toInputDateTime(value) {
        if (!value || typeof value !== 'string') {
            return '';
        }
        return value.slice(0, 16);
    }

    function createSelect(name, options, selectedValue, placeholder) {
        const select = document.createElement('select');
        select.name = name;
        if (placeholder) {
            const option = document.createElement('option');
            option.value = '';
            option.textContent = placeholder;
            option.disabled = true;
            if (!selectedValue) {
                option.selected = true;
            }
            select.appendChild(option);
        }
        options.forEach((value) => {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value;
            if (value === selectedValue) {
                option.selected = true;
            }
            select.appendChild(option);
        });
        return select;
    }

    function createUpdateForm(event, options = {}) {
        const { submitLabel = 'Aggiorna evento' } = options;
        const form = document.createElement('form');
        form.className = 'event-card__form';
        form.dataset.action = 'update';
        form.dataset.eventId = event.id;

        const titleRow = document.createElement('div');
        titleRow.className = 'event-card__form-row';
        const titleInput = document.createElement('input');
        titleInput.type = 'text';
        titleInput.name = 'title';
        titleInput.placeholder = 'Titolo';
        titleInput.value = event.title ?? '';
        titleRow.appendChild(titleInput);
        form.appendChild(titleRow);

        const descriptionRow = document.createElement('div');
        descriptionRow.className = 'event-card__form-row';
        const description = document.createElement('textarea');
        description.name = 'description';
        description.placeholder = 'Descrizione';
        description.rows = 3;
        description.value = event.description ?? '';
        descriptionRow.appendChild(description);
        form.appendChild(descriptionRow);

        const typeRow = document.createElement('div');
        typeRow.className = 'event-card__form-row';
        const typeSelect = createSelect('type', eventTypes, event.type, null);
        typeRow.appendChild(typeSelect);
        form.appendChild(typeRow);

        const locationRow = document.createElement('div');
        locationRow.className = 'event-card__form-row';
        const locationInput = document.createElement('input');
        locationInput.type = 'text';
        locationInput.name = 'location';
        locationInput.placeholder = 'Località';
        const locationAddress = extractLocationAddress(event.location);
        locationInput.value = locationAddress;
        locationRow.appendChild(locationInput);
        form.appendChild(locationRow);

        const latInput = document.createElement('input');
        latInput.type = 'hidden';
        latInput.name = 'lat';
        latInput.value = event.location?.lat ?? '';
        locationRow.appendChild(latInput);

        const lngInput = document.createElement('input');
        lngInput.type = 'hidden';
        lngInput.name = 'lng';
        lngInput.value = event.location?.lng ?? '';
        locationRow.appendChild(lngInput);

        const timingRow = document.createElement('div');
        timingRow.className = 'event-card__form-row';
        const startInput = document.createElement('input');
        startInput.type = 'datetime-local';
        startInput.name = 'startDateTime';
        startInput.value = toInputDateTime(event.startDateTime);
        const endInput = document.createElement('input');
        endInput.type = 'datetime-local';
        endInput.name = 'endDateTime';
        endInput.value = toInputDateTime(event.endDateTime);
        timingRow.append(startInput, endInput);
        form.appendChild(timingRow);

        const priceRow = document.createElement('div');
        priceRow.className = 'event-card__form-row';
        const priceInput = document.createElement('input');
        priceInput.type = 'number';
        priceInput.name = 'price';
        priceInput.min = '0';
        priceInput.step = '0.01';
        priceInput.value = event.price ?? '';
        priceRow.appendChild(priceInput);
        const submitButton = document.createElement('button');
        submitButton.type = 'submit';
        submitButton.className = 'button';
        submitButton.textContent = submitLabel;
        priceRow.appendChild(submitButton);
        form.appendChild(priceRow);

        return form;
    }

    function createStateForm(event)
    {
        const form = document.createElement('form');
        form.className = 'event-card__form';
        form.dataset.action = 'update-state';
        form.dataset.eventId = event.id;
        const row = document.createElement('div');
        row.className = 'event-card__form-row';
        const select = createSelect('state', eventStates, event.state, null);
        row.appendChild(select);
        const button = document.createElement('button');
        button.type = 'submit';
        button.className = 'button button--secondary';
        button.textContent = 'Aggiorna stato';
        row.appendChild(button);
        form.appendChild(row);
        return form;
    }

    function createDeleteForm(eventId) {
        const form = document.createElement('form');
        form.className = 'event-card__form';
        form.dataset.action = 'delete';
        form.dataset.eventId = eventId;
        const row = document.createElement('div');
        row.className = 'event-card__form-row';
        const button = document.createElement('button');
        button.type = 'submit';
        button.className = 'button button--ghost';
        button.textContent = 'Elimina evento';
        row.appendChild(button);
        form.appendChild(row);
        return form;
    }

    function closeModal() {
        const overlay = document.querySelector('.modal-overlay');
        if (overlay) {
            overlay.remove();
            document.body.classList.remove('modal-open');
        }
    }

    function showEditModal(event) {
        closeModal();

        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';

        const dialog = document.createElement('div');
        dialog.className = 'modal';

        const header = document.createElement('div');
        header.className = 'modal__header';

        const title = document.createElement('h3');
        title.className = 'modal__title';
        title.textContent = 'Modifica evento';

        const closeBtn = document.createElement('button');
        closeBtn.type = 'button';
        closeBtn.className = 'modal__close';
        closeBtn.textContent = '×';
        closeBtn.addEventListener('click', closeModal);

        header.append(title, closeBtn);

        const body = document.createElement('div');
        body.className = 'modal__body';

        const editForm = createUpdateForm(event, { submitLabel: 'Conferma modifiche' });
        body.appendChild(editForm);

        const stateWrapper = document.createElement('div');
        stateWrapper.className = 'modal__section';
        const stateLabel = document.createElement('p');
        stateLabel.className = 'modal__section-title';
        stateLabel.textContent = 'Stato evento';
        stateWrapper.appendChild(stateLabel);
        stateWrapper.appendChild(createStateForm(event));
        body.appendChild(stateWrapper);

        dialog.append(header, body);
        overlay.appendChild(dialog);
        document.body.appendChild(overlay);
        document.body.classList.add('modal-open');

        overlay.addEventListener('click', (evt) => {
            if (evt.target === overlay) {
                closeModal();
            }
        });
    }


    function includesId(list, id) {
        if (!Array.isArray(list) || id == null) {
            return false;
        }
        return list.includes(id);
    }

    function createActionSection(event) {
        const actions = document.createElement('div');
        actions.className = 'event-card__actions';
        let hasAction = false;

        if (currentUserRole === 'BUYER' && currentUserId) {
            if (includesId(event.buyerIds, currentUserId)) {
                const row = document.createElement('div');
                row.className = 'event-card__form-row';
                const info = document.createElement('p');
                info.className = 'text-muted';
                info.textContent = 'Hai già acquistato questo evento.';
                row.appendChild(info);
                actions.appendChild(row);
            } else {
                const form = document.createElement('form');
                form.className = 'event-card__form';
                form.dataset.action = 'book';
                form.dataset.eventId = event.id;
                form.dataset.paymentUrl = `/psp/paymentdemo/payment?referenceId=${event.id}`;

                const row = document.createElement('div');
                row.className = 'event-card__form-row';
                const orderButton = document.createElement('button');
                orderButton.type = 'submit';
                orderButton.className = 'button';
                orderButton.textContent = `Acquista evento (${formatCurrency(event.price, event.currencyCode)})`;
                row.appendChild(orderButton);

                form.appendChild(row);
                actions.appendChild(form);
                hasAction = true;
            }
        }

        if (sellerRoles.includes(currentUserRole) && currentUserId &&
            includesId(event.invitedSellerIds, currentUserId) &&
            !includesId(event.confirmedSellerIds, currentUserId)) {
            const form = document.createElement('form');
            form.className = 'event-card__form';
            form.dataset.action = 'confirm-seller';
            form.dataset.eventId = event.id;
            const row = document.createElement('div');
            row.className = 'event-card__form-row';
            const button = document.createElement('button');
            button.type = 'submit';
            button.className = 'button';
            button.textContent = 'Conferma partecipazione';
            row.appendChild(button);
            form.appendChild(row);
            actions.appendChild(form);
            hasAction = true;
        }

        if (currentUserId && Number(event.entertainerId) === currentUserId) {
            const management = document.createElement('details');
            management.className = 'event-card__management';

            const summary = document.createElement('summary');
            summary.textContent = 'Gestisci evento';
            management.appendChild(summary);

            const info = document.createElement('p');
            info.className = 'text-muted';
            info.textContent = 'Invita rapidamente oppure modifica ed elimina l\'evento.';
            management.appendChild(info);

            const inviteForm = document.createElement('form');
            inviteForm.className = 'event-card__form event-card__form--inline';
            inviteForm.dataset.action = 'invite-by-email';
            inviteForm.dataset.eventId = event.id;

            const inviteRow = document.createElement('div');
            inviteRow.className = 'event-card__form-row';

            const emailInput = document.createElement('input');
            emailInput.type = 'email';
            emailInput.name = 'email';
            emailInput.placeholder = 'Email da invitare';
            inviteRow.appendChild(emailInput);

            const inviteButton = document.createElement('button');
            inviteButton.type = 'submit';
            inviteButton.className = 'button';
            inviteButton.textContent = 'Invita';
            inviteRow.appendChild(inviteButton);

            inviteForm.appendChild(inviteRow);
            management.appendChild(inviteForm);

            const managementActions = document.createElement('div');
            managementActions.className = 'event-card__management-actions';

            const editButton = document.createElement('button');
            editButton.type = 'button';
            editButton.className = 'button button--secondary';
            editButton.dataset.editEventId = event.id;
            editButton.textContent = 'Modifica evento';
            managementActions.appendChild(editButton);

            const deleteForm = createDeleteForm(event.id);
            deleteForm.classList.add('event-card__delete-form');
            managementActions.appendChild(deleteForm);

            management.appendChild(managementActions);

            actions.appendChild(management);
            hasAction = true;
        }


        return hasAction ? actions : null;
    }

    function renderEvents(events) {
        if (!eventsContainer) {
            return;
        }
        eventsContainer.innerHTML = '';
        if (!Array.isArray(events) || events.length === 0) {
            const empty = document.createElement('p');
            empty.className = 'text-muted';
            empty.textContent = 'Non ci sono eventi disponibili al momento.';
            eventsContainer.appendChild(empty);
            return;
        }
        const sorted = [...events].sort((a, b) => {
            const aDate = new Date(a.startDateTime || 0).getTime();
            const bDate = new Date(b.startDateTime || 0).getTime();
            return aDate - bDate;
        });
        sorted.forEach((event) => {
            const card = document.createElement('article');
            card.className = 'event-card';

            const header = document.createElement('div');
            header.className = 'event-card__header';
            const title = document.createElement('h3');
            title.className = 'event-card__title';
            title.textContent = event.title;
            const badge = document.createElement('span');
            badge.className = `badge ${getBadgeClass(event.state)}`;
            badge.textContent = event.state;
            header.append(title, badge);
            card.appendChild(header);

            const description = document.createElement('p');
            description.className = 'event-card__description';
            description.textContent = event.description;
            card.appendChild(description);

            const meta = document.createElement('div');
            meta.className = 'event-card__meta';
            meta.appendChild(createMetaRow('Tipologia', event.type));
            const locationLabel = extractLocationAddress(event.location) || 'N/D';
            meta.appendChild(createMetaRow('Località', locationLabel));
            meta.appendChild(createMetaRow('Inizio', formatDateTime(event.startDateTime)));
            meta.appendChild(createMetaRow('Fine', formatDateTime(event.endDateTime)));
            meta.appendChild(createMetaRow('Prezzo', formatCurrency(event.price, event.currencyCode)));            meta.appendChild(createMetaRow('Animatore', event.entertainerEmail ? `${event.entertainerEmail}` : 'N/D'));
            card.appendChild(meta);

            card.appendChild(createParticipantsSection(event));

            const actions = createActionSection(event);
            if (actions) {
                card.appendChild(actions);
            }

            eventsContainer.appendChild(card);
        });
    }

    async function loadEvents() {
        if (!eventsContainer) {
            return;
        }

        eventsContainer.innerHTML = '<p class="text-muted">Caricamento in corso...</p>';

        const response = await fetch(API_BASE, { headers: { 'Accept': 'application/json' } });

        if (!response.ok) {
            const msg = await extractError(response);
            showFeedback(msg || 'Impossibile caricare gli eventi.', 'error');

            const errorMessage = document.createElement('p');
            errorMessage.className = 'text-muted';
            errorMessage.textContent = 'Impossibile caricare gli eventi.';
            eventsContainer.innerHTML = '';
            eventsContainer.appendChild(errorMessage);

            return;
        }

        const events = await response.json();
        eventsCache = Array.isArray(events) ? events : [];
        renderEvents(eventsCache);
    }

    async function handleCreateEvent(event) {
        event.preventDefault();
        if (!createEventForm) {
            return;
        }
        if (!currentUserId) {
            showFeedback('Impossibile identificare l\'utente corrente.', 'error');
            return;
        }
        if (currentUserRole !== 'ENTERTAINER') {
            showFeedback('Solo un animatore può creare un evento.', 'error');
            return;
        }
        clearFeedback();
        const formData = new FormData(createEventForm);
        const location = buildLocationPayload(formData);
        if (!location) {
            showFeedback('Specifica una località valida.', 'error');
            return;
        }
        const payload = {
            title: formData.get('title'),
            description: formData.get('description'),
            type: formData.get('type'),
            location,
            startDateTime: formData.get('startDateTime'),
            endDateTime: formData.get('endDateTime'),
            price: formData.get('price') ? Number(formData.get('price')) : 0
        };
        try {
            await sendRequest(`${API_BASE}/entertainers/${currentUserId}`, 'POST', payload);
            createEventForm.reset();
            showFeedback('Evento creato con successo.', 'success');
            await loadEvents();
        } catch (error) {
            showFeedback(error.message || 'Errore durante la creazione dell\'evento.', 'error');
        }
    }

    function buildLocationPayload(formData) {
        const address = (formData.get('location') || '').toString().trim();
        const latRaw = formData.get('lat');
        const lngRaw = formData.get('lng');

        const location = {};

        if (address) {
            location.address = address;
        }

        const lat = latRaw !== null && latRaw !== '' ? Number(latRaw) : null;
        if (Number.isFinite(lat)) {
            location.lat = lat;
        }

        const lng = lngRaw !== null && lngRaw !== '' ? Number(lngRaw) : null;
        if (Number.isFinite(lng)) {
            location.lng = lng;
        }

        return Object.keys(location).length > 0 ? location : null;
    }

    function buildUpdatePayload(form) {
        const formData = new FormData(form);
        const payload = {};
        const location = buildLocationPayload(formData);
        if (location) {
            payload.location = location;
        }

        const title = (formData.get('title') || '').toString().trim();
        if (title) {
            payload.title = title;
        }

        const description = (formData.get('description') || '').toString().trim();
        if (description) {
            payload.description = description;
        }

        const type = (formData.get('type') || '').toString().trim();
        if (type) {
            payload.type = type;
        }

        const startDateTime = (formData.get('startDateTime') || '').toString().trim();
        if (startDateTime) {
            payload.startDateTime = startDateTime;
        }

        const endDateTime = (formData.get('endDateTime') || '').toString().trim();
        if (endDateTime) {
            payload.endDateTime = endDateTime;
        }

        const priceValue = formData.get('price');
        if (priceValue !== null && priceValue !== undefined && priceValue !== '') {
            const numericPrice = Number(priceValue);
            if (!Number.isNaN(numericPrice)) {
                payload.price = numericPrice;
            }
        }
        return payload;
    }

    async function handleActionSubmit(event) {
        const form = event.target;
        if (!(form instanceof HTMLFormElement) || !form.dataset.action) {
            return;
        }
        event.preventDefault();
        clearFeedback();

        const action = form.dataset.action;
        const eventId = form.dataset.eventId;
        if (!eventId) {
            showFeedback('Evento non valido.', 'error');
            return;
        }

        const actionsRequiringUser = [
            'book',
            'confirm-seller',
            'update',
            'update-state',
            'invite-by-email',
            'delete'
        ];

        if (actionsRequiringUser.includes(action) && !currentUserId) {
            showFeedback('Utente non identificato.', 'error');
            return;
        }

        try {
            switch (action) {
                case 'book':
                    await sendRequest(`${API_BASE}/${eventId}/buyers/${currentUserId}/bookings`, 'POST');
                    showFeedback('Prenotazione effettuata con successo.', 'success');
                    break;

                case 'confirm-seller':
                    await sendRequest(`${API_BASE}/${eventId}/sellers/${currentUserId}/confirm`, 'POST');
                    showFeedback('Partecipazione confermata.', 'success');
                    break;

                case 'update': {
                    const payload = buildUpdatePayload(form);
                    if (Object.keys(payload).length === 0) {
                        showFeedback('Nessuna modifica da salvare.', 'error');
                        return;
                    }
                    await sendRequest(`${API_BASE}/${eventId}/entertainers/${currentUserId}`, 'PUT', payload);
                    showFeedback('Evento aggiornato con successo.', 'success');
                    break;
                }

                case 'update-state': {
                    const state = new FormData(form).get('state');
                    if (!state) {
                        showFeedback('Seleziona uno stato valido.', 'error');
                        return;
                    }
                    await sendRequest(
                        `${API_BASE}/${eventId}/entertainers/${currentUserId}/state`,
                        'PATCH',
                        { state }
                    );
                    showFeedback('Stato aggiornato con successo.', 'success');
                    break;
                }

                case 'invite-by-email': {
                    const formData = new FormData(form);
                    const email = (formData.get('email') || '').toString().trim();
                    if (!email) {
                        showFeedback('Inserisci una email valida.', 'error');
                        return;
                    }
                    await sendRequest(
                        `${API_BASE}/${eventId}/entertainers/${currentUserId}/invite-by-email`,
                        'POST',
                        { email }
                    );
                    form.reset();
                    showFeedback('Invito inviato correttamente.', 'success');
                    break;
                }

                case 'delete':
                    await sendRequest(`${API_BASE}/${eventId}/entertainers/${currentUserId}`, 'DELETE');
                    showFeedback('Evento eliminato correttamente.', 'success');
                    break;

                default:
                    return;
            }

            await loadEvents();

            if (form.closest('.modal-overlay')) {
                closeModal();
            }
        } catch (error) {
            showFeedback(error.message || 'Operazione non riuscita.', 'error');
        }
    }


    function handleEventClick(event) {
        const editButton = event.target.closest('[data-edit-event-id]');
        if (!editButton) {
            return;
        }
        const eventId = editButton.dataset.editEventId;
        const eventData = eventId ? findEvent(eventId) : null;
        if (!eventData) {
            showFeedback('Impossibile trovare i dettagli dell\'evento selezionato.', 'error');
            return;
        }
        showEditModal(eventData);
    }

    if (createEventForm) {
        createEventForm.addEventListener('submit', handleCreateEvent);
    }

    document.addEventListener('submit', handleActionSubmit);

    if (eventsContainer) {
        eventsContainer.addEventListener('click', handleEventClick);
    }

    if (createEventSection && createEventToggleBtn) {
        createEventToggleBtn.addEventListener('click', () => {
            createEventSection.hidden = !createEventSection.hidden;
        });
    }


    loadEvents();
})();
