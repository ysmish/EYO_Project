let labels = {};
let nextId = 1;

const createLabel = (username, name) => {
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

const getLabelById = (username, labelId) => {
    if (!labels[username] || !labels[username][labelId]) {
        return null;
    }
    return { id: labelId, ...labels[username][labelId] };
};

const getAllLabels = (username) => {
    if (!labels[username]) {
        return [];
    }
    return Object.entries(labels[username])
        .map(([id, label]) => ({ id: parseInt(id), ...label }));
};

const updateLabel = (username, labelId, updates) => {
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }

    if (updates.name) {
        labels[username][labelId].name = updates.name;
    }
    return true;
};

const deleteLabel = (username, labelId) => {
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }

    delete labels[username][labelId];
    return true;
};

export { labels, createLabel, getLabelById, getAllLabels, updateLabel, deleteLabel };