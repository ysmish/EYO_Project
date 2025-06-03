let labels = {};
let nextId = 1;

const createNewLabel = (username, name) => {
    if (!name) {
        throw new Error('Name is required');
    }

    // Initialize user's labels if they don't exist
    if (!labels[username]) {
        labels[username] = {};
    }

    // Create new label with unique ID
    const labelId = nextId++;
    labels[username][labelId] = {
        name,
        mails: []
    };

    return labelId;
};

const getLabel = (username, labelId) => {
    if (!labels[username] || !labels[username][labelId]) {
        return null;
    }
    return { id: labelId, ...labels[username][labelId] };
};

const getLabels = (username) => {
    if (!labels[username]) {
        return [];
    }
    return Object.entries(labels[username])
        .map(([id, label]) => ({ id: parseInt(id), ...label }));
};

const changeLabel = (username, labelId, updates) => {
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }

    if (updates.name) {
        labels[username][labelId].name = updates.name;
    }
    return true;
};

const removeLabel = (username, labelId) => {
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }

    delete labels[username][labelId];
    return true;
};

export { labels, createNewLabel, getLabel, getLabels, changeLabel, removeLabel };