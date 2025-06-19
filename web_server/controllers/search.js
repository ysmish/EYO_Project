import e from 'express';
import { search } from '../models/search.js';
import { authorizeToken } from '../models/tokens.js';
import { getLabels } from '../models/labels.js';

const searchQuery = (req, res) => {
    const token = req.headers.authorization;
    const query = decodeURIComponent(req.params.query);
    if (!token || !query) {
        return res.status(400).json({ error: 'Authorization token and query are required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    const querys = query.split(' ').filter(q => q.length > 0);
    const validLabels = getLabels(username);
    let labels = [];
    let keywords = [];
    let flags = {
        'inbox': false,
        'sent': false,
        'starred': false,
        'drafts': false,
        'spam': false,
    }
    for (let q of querys) {
        if (q.startsWith('label:')) {
            const labelName = q.substring(6).toLowerCase();
            const label = validLabels.find(label => label.name.toLowerCase() === labelName);
            if (label) {
                labels.push(label.id);
            } else {
                return res.status(200).json([]); // Return empty array if label not found
            }
        } else if (q.startsWith('in:')) {
            const flag = q.substring(3).toLowerCase();
            if (flags.hasOwnProperty(flag)) {
                flags[flag] = true;
            } else {
                return res.status(200).json([]); // Return empty array if flag is invalid
            }
        } else {
            keywords.push(q.toLowerCase());
        }
    }
    try {
        const results = search(username, keywords, flags, labels);
        return res.status(200).json(results);
    } catch (error) {
        console.error('Search error:', error);
        return res.status(500).json({ error: 'Internal server error.' });
    }
}
export { searchQuery };