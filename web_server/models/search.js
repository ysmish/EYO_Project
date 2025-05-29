import { mails } from './mails.js';
const search = (username, query) => {
    // Normalize the query to lowercase for case-insensitive search
    const normalizedQuery = query.toLowerCase();

    // Filter mails based on the query
    return mails.filter(mail => {
        if (mail.from !== username && mail.to !== username && !mail.cc.includes(username)) {
            return false; // User must be either the sender, recipient, or in CC
        }
        for (const key in mail) {
            if (Array.isArray(mail[key]) && mail[key].some(item => toString(item).toLowerCase().includes(normalizedQuery))) {
                return true; // Match found in any array field
            }
            if (toString(mail[key]).toLowerCase().includes(normalizedQuery)) {
                return true; // Match found in non-array field
            }
        }
        return false; // No match found
    });
};



export { search };