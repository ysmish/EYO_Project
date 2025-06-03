import { createLabel, getLabelById, getAllLabels, updateLabel, deleteLabel } from '../models/labels.js';

const getAllLabels = (req, res) => {
    return res.status(200).json([]);
}

const getLabelById = (req, res) => {
    return res.status(200).json({});
}

const createLabel = (req, res) => {
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }

    const { name } = req.body;

    // Basic validation
    if (!name) {
        return res.status(400).json({ error: 'Name is required' });
    }

    try {
        const labelId = createLabel(username, name);
        return res.status(201).location(`/api/labels/${labelId}`).end();
    } catch (error) {
        console.error('Error creating label:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
}

const patchLabel = (req, res) => {
    return res.status(200).json({
        message: 'Add Label endpoint is not implemented yet'
    });
}

const deleteLabel = (req, res) => {
    return res.status(200).json({
        message: 'Add Label endpoint is not implemented yet'
    });
}

export { getAllLabels, getLabelById, createLabel, patchLabel, deleteLabel };