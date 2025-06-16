import { search } from '../models/search.js';
import { authorizeToken } from '../models/tokens.js';

const searchQuery = (req, res) => {
    const token = req.headers.authorization;
    const query = req.params.query;
    if (!token || !query) {
        return res.status(400).json({ error: 'Authorization token and query are required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    try {
        const results = search(username, query);
        return res.status(200).json(results);
    } catch (error) {
        console.error('Search error:', error);
        return res.status(500).json({ error: 'Internal server error.' });
    }
}
export { searchQuery };