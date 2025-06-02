import { mails } from './mails.js';
const search = (username, query) => {
    if (!mails[username]) {
        return [];
    }
    // Normalize the query to lowercase for case-insensitive search
    const normalizedQuery = query.toLowerCase();

    return Object.entries(mails[username])
        .map(([id, mail]) => {
            mail = { id: parseInt(id), ...mail }
            for (const key in mail) {
                if (Array.isArray(mail[key]) && mail[key].some(item => toString(item).toLowerCase().includes(normalizedQuery))) {
                    return mail;
                }
                if (toString(mail[key]).toLowerCase().includes(normalizedQuery)) {
                    return mail;
                }
            }
            return null;
        })
        .filter(mail => mail !== null)
        .sort((a, b) => new Date(b.date) - new Date(a.date));
};



export { search };