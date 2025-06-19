import { mails } from './mails.js';
const search = (username, keywords, flags, labels) => {
    if (!mails[username]) {
        return [];
    }
    // If no keywords are given, match all mails
    const hasKeywords = keywords && keywords.length > 0;
    const normalizedKeywords = hasKeywords ? keywords.map(keyword => keyword.toLowerCase()) : [];

    return Object.entries(mails[username])
        .map(([id, mail]) => {
            mail = { id: parseInt(id), ...mail }
            if (!hasKeywords) {
                return mail;
            }
            for (const key in mail) {
                if (key === 'labels') continue; // Skip labels field for keyword search
                if (Array.isArray(mail[key]) && mail[key].some(item =>
                    normalizedKeywords.some(keyword => item.toLowerCase().includes(keyword))
                )) {
                    return mail;
                }
                if (typeof mail[key] === 'string' && normalizedKeywords.some(keyword =>
                    mail[key].toLowerCase().includes(keyword)
                )) {
                    return mail;
                }
            }
            return null;
        })
        .filter(mail => mail !== null)
        .filter(mail => {
            // If 'all' flag is set, return all mails without filtering
            if (flags.all) {
                return true;
            }

            // Check if the mail matches the flags
            if (flags.inbox && !mail.labels.includes('Inbox')) return false;
            if (flags.sent && !mail.labels.includes('Sent')) return false;
            if (flags.starred && !mail.labels.includes('Starred')) return false;
            if (flags.drafts && !mail.labels.includes('Drafts')) return false;
            if (flags.spam && !mail.labels.includes('Spam')) return false;

            // Check if the mail matches all of the labels
            if (labels.length > 0 && !labels.every(label => mail.labels.includes(label))) {
                return false;
            }

            return true;
        })
        .sort((a, b) => new Date(b.date) - new Date(a.date));
};

export { search };