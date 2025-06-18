let labels = {
    "Mro": {
        'Inbox': {
            id: 'Inbox',
            name: 'Inbox'
        },
        'Sent': {
            id: 'Sent',
            name: 'Sent'
        },
        'Starred': {
            id: 'Starred',
            name: 'Starred'
        },
        'Drafts': {
            id: 'Drafts',
            name: 'Drafts'
        },
        'Spam': {
            id: 'Spam',
            name: 'Spam'
        },
        1: {
            id: 1,
            name: 'Work'
        },
        2: {
            id: 2,
            name: 'Personal'
        }
    }
};
let labelIdCounter = 1;  // Counter for generating unique label IDs

const createNewLabel = (username, name) => {
    if (!name) {
        throw { error: 'Label name is required' };
    }

    // Initialize user's labels if they don't exist
    if (!labels[username]) {
        labels[username] = {};
    }

    // Check if label name already exists
    if (Object.values(labels[username]).some(label => label.name === name)) {
        return { error: 'Label name already exists' };
    }

    // Create new label with unique ID
    const id = labelIdCounter++;
    labels[username][id] = {
        id: id,
        name: name,
    };

    return { id, name };
};

const getLabel = (username, labelId) => {
    if (!labels[username]) {
        return null;
    }
    
    labelId = parseInt(labelId);
    if (!labels[username][labelId]) {
        return null;
    }

    if (isNaN(labelId)) {
        return { error: 'Invalid label ID' };
    }
    
    const label = labels[username][labelId];
    return {
        id: label.id,
        name: label.name
    };
};

const getLabels = (username) => {
    if (!labels[username]) {
        return [];
    }
    return Object.values(labels[username]).map(label => ({
        id: label.id,
        name: label.name
    })).filter(label => isNaN(label.id) === false); // Ensure IDs are valid numbers
};

const changeLabel = (username, labelId, updates) => {
    labelId = parseInt(labelId);
    if (!labels[username] || !labels[username][labelId]) {
        return { error: 'Label not found' };
    }

    if (!updates.name) {
        return { error: 'New label name is required' };
    }

    // Check if the new name already exists (except for this label)
    if (Object.values(labels[username]).some(
        label => label.id !== labelId && label.name === updates.name
    )) {
        return { error: 'Label name already exists' };
    }

    // Update the label name
    labels[username][labelId].name = updates.name;
    return { 
        id: labelId,
        name: updates.name 
    };
};

const removeLabel = (username, labelId) => {
    labelId = parseInt(labelId);
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }
    
    delete labels[username][labelId];
    return true;
};

export { labels, createNewLabel, getLabel, getLabels, changeLabel, removeLabel };