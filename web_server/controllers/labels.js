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
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }

    const labelId = req.params.id;
    if (!labelId) {
        return res.status(400).json({ error: 'Label ID is required' });
    }

    try {
        const label = getLabel(username, labelId);
        if (!label) {
            return res.status(404).json({ error: 'Label not found' });
        }
        return res.status(200).json(label);
    } catch (error) {
        console.error('Error fetching label:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
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
        const result = createNewLabel(username, name);
        if (result.error) {
            return res.status(400).json({ error: result.error });
        }
        // Set the Location header to point to the new label's URL
        return res.status(201)
                 .location(`/api/labels/${result.id}`).end();
    } catch (error) {
        console.error('Error creating label:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
}

const patchLabel = (req, res) => {
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required' });
    }

    // Get label ID from params (which is the current name)
    const labelId = req.params.id;
    if (!labelId) {
        return res.status(400).json({ error: 'Label ID is required' });
    }

    // Get updates from request body
    const updates = req.body;
    if (!updates || !updates.name) {
        return res.status(400).json({ error: 'New label name is required' });
    }

    try {
        const result = changeLabel(username, labelId, updates);
        if (result.error) {
            return res.status(400).json({ error: result.error });
        }
        return res.status(204).end();
    } catch (error) {
        console.error('Error updating label:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
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