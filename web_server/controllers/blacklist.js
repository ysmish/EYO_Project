import { addUrl, checkUrl, deleteUrl } from '../models/blacklist.js';


const addURL = (req, res) => {
    const url = req.body.url;
    if (!url) {
        return res.status(400).json({ error: 'URL is required' });
    }

    // first check if the url is in the blacklist
    try {
        if (checkUrl(url)) {
            return res.status(400).json({ error: 'URL already in blacklist' });
        }
    } catch (error) {
        return res.status(500).json({ error: 'Failed to check URL' });
    }

    // if the url is not in the blacklist, add it to the blacklist
    try {
        const result = addUrl(url);
        if (result) {
            return res.status(201).json({ message: 'URL added successfully' });
        }
        else {
            return res.status(400).json({ error: 'URL is not valid' });
        }
    } catch (error) {
        return res.status(500).json({ error: 'Failed to add URL' });
    }
}

const deleteURL = (req, res) => {
    const url = req.body.url;
    try {
        const result = deleteUrl(url);
        if (result) {
            return res.status(204).json({
                message: 'URL deleted successfully'
            });
        } else {
            return res.status(404).json({
                message: 'URL not found'
            });
        }
    } catch (err) {
        return res.status(500).json({
            message: 'Internal server error'
        });
    }
}

export { addURL, deleteURL };