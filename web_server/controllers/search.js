import { search } from '../models/search.js';

const searchQuery = (req, res) => {
    const username = req.headers.authorization;
    const query = req.params.query;
    if (!username || !query) {
        return res.status(400).json({ error: 'Username and query are required.' });
    }

    try {
        const results = search(username, query);
        return res.status(200).json(results);
    } catch (error) {
        console.error('Search error:', error);
        return res.status(500).json({ error: 'Internal server error.' });
    }
}
export { searchQuery };