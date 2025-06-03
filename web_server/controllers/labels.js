import { createNewLabel, getLabel, getLabels, changeLabel, removeLabel } from '../models/labels.js';

const getAllLabels = (req, res) => {
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }

    try {
        const labels = getLabels(username);
        return res.status(200).json(labels);
    } catch (error) {
        console.error('Error fetching labels:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
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
        const label = createNewLabel(username, name)
        if (label?.error) {
            return res.status(400).json({ error: label.error });
        }
        return res.status(201).location(`/api/labels/${name}`).end();
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
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }

    // Get label name from params
    const labelName = req.params.id;
    if (!labelName) {
        return res.status(400).json({ error: 'Label name is required' });
    }

    try {
        const success = removeLabel(username, labelName);
        if (!success) {
            return res.status(404).json({ error: 'Label not found' });
        }
        return res.status(204).end();
    } catch (error) {
        console.error('Error deleting label:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
}

export { getAllLabels, getLabelById, createLabel, patchLabel, deleteLabel };