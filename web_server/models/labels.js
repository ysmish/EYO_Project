import { mails } from './mails.js';  // Import mails model to manage mail-label relationships
let labels = {};

let labelIdCounter = 1;  // Counter for generating unique label IDs

const createNewLabel = (username, name, color) => {
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
        mails: [],  // Initialize with an empty array for mails
        color: color || '#4F46E5'  // Default color if none provided
    };

    return { id, name, color: color || '#4F46E5' };
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
        name: label.name,
        color: label.color
    };
};

const getLabels = (username) => {
    if (!labels[username]) {
        return [];
    }
    return Object.values(labels[username]).map(label => ({
        id: label.id,
        name: label.name,
        color: label.color
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

    // Update the label name and color if provided
    labels[username][labelId].name = updates.name;
    if (updates.color) {
        labels[username][labelId].color = updates.color;
    }
    return { 
        id: labelId,
        name: updates.name,
        mails: labels[username][labelId].mails,
        color: labels[username][labelId].color
    };
};

const removeLabel = (username, labelId) => {
    labelId = parseInt(labelId);
    if (!labels[username] || !labels[username][labelId]) {
        return false;
    }
    for (const mailId of labels[username][labelId].mails) {
        // Remove this label from each mail's labels
        if (mails[username] && mails[username][mailId]) {
            const mail = mails[username][mailId];
            mail.labels = mail.labels.filter(label => label !== labelId);
        }
    }
    // Delete the label from the user's labels
    delete labels[username][labelId];
    return true;
};

export { labels, createNewLabel, getLabel, getLabels, changeLabel, removeLabel };