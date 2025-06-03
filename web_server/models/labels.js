let labels = {};

const createNewLabel = (username, name) => {
    if (!name) {
        throw { error: 'Label name is required' };
    }

    // Initialize user's labels if they don't exist
    if (!labels[username]) {
        labels[username] = {};
    }

    if (Object.keys(labels[username]).some(label => label === name)) {
        return { error: 'Label name already exists' };
    };

    labels[username][name] = [];
    return name;
};

const getLabel = (username, name) => {
    if (!labels[username]) {
        return null;
    }
    
    if (!labels[username][name]) {
        return null;
    }
    
    return {
        name: name,
    };
};

const getLabels = (username) => {
    if (!labels[username]) {
        return [];
    }
    return Object.keys(labels[username]);
};

const changeLabel = (username, labelId, updates) => {
};

const removeLabel = (username, labelId) => {
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }
    
    delete labels[username][labelId];
    return true;
};

export { labels, createNewLabel, getLabel, getLabels, changeLabel, removeLabel };