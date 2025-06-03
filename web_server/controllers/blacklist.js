import { addUrl, checkUrl, deleteUrl } from '../models/blacklist.js';
import { getUser } from '../models/users.js';

const addURL = async (req, res) => {
    const username = req.headers.authorization;
    if (!username) {
        return res.status(401).json({ error: 'Username is required' });
    }

    // Verify user exists
    const user = getUser(username);
    if (user.error) {
        return res.status(401).json({ error: 'Invalid user' });
    }

    const url = req.body.url;
    if (!url) {
        return res.status(400).json({ error: 'URL is required' });
    }

    try {
        // first check if the url is in the blacklist
        const exists = await checkUrl(url);
        if (exists) {
            return res.status(200).json({ error: 'URL already in blacklist' });
        }

        // if the url is not in the blacklist, add it
        const result = await addUrl(url);
        if (result === null) {
            return res.status(503).json({ error: 'URL server is unavailable' });
        }
        if (result) {
            return res.status(201).json({ message: 'URL added successfully' });
        } else {
            return res.status(400).json({ error: 'Failed to add URL to blacklist' });
        }
    } catch (error) {
        console.error('Error in addURL:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

const deleteURL = async (req, res) => {
    const username = req.headers.authorization;
    if (!username) {
        return res.status(401).json({ error: 'Username is required' });
    }

    // Verify user exists
    const user = getUser(username);
    if (user.error) {
        return res.status(401).json({ error: 'Invalid user' });
    }

    const url = req.params.id;
    if (!url) {
        return res.status(400).json({ error: 'URL is required' });
    }

    try {
        // Check if URL exists before trying to delete
        const exists = await checkUrl(url);
        if (exists === null) {
            return res.status(503).json({ error: 'URL server is unavailable' });
        }
        if (!exists) {
            return res.status(404).json({ error: 'URL not found in blacklist' });
        }

        const result = await deleteUrl(url);
        if (result === null) {
            return res.status(503).json({ error: 'URL server is unavailable' });
        }
        if (result) {
            return res.status(204).send();
        } else {
            return res.status(500).json({ error: 'Failed to delete URL from blacklist' });
        }
    } catch (error) {
        console.error('Error in deleteURL:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

export { addURL, deleteURL };