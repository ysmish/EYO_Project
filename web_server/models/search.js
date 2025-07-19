import Mail from '../service/Mail.js';

const search = async (username, keywords, flags, labels) => {
    try {
        // Build the search query
        let query = { owner: username };
        
        // If no keywords are given, match all mails
        const hasKeywords = keywords && keywords.length > 0;
        
        if (hasKeywords) {
            const keywordRegex = keywords.map(keyword => 
                new RegExp(keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'i')
            );
            
            query.$or = [
                { from: { $in: keywordRegex } },
                { to: { $in: keywordRegex } },
                { cc: { $in: keywordRegex } },
                { subject: { $in: keywordRegex } },
                { body: { $in: keywordRegex } }
            ];
        }

        // Fetch mails from database
        const mails = await Mail.find(query).sort({ date: -1 });
        
        return mails
            .map(mail => ({
                id: mail.mailId,
                from: mail.from,
                to: mail.to,
                cc: mail.cc,
                subject: mail.subject,
                body: mail.body,
                date: mail.date,
                read: mail.read,
                attachments: mail.attachments,
                labels: mail.labels
            }))
            .filter(mail => {
                // If 'all' flag is set, return all mails without filtering
                if (flags.all) {
                    return true;
                }

                // Check if the mail matches the flags
                if (flags.inbox && !mail.labels.includes(1)) return false;
                if (flags.sent && !mail.labels.includes(2)) return false;
                if (flags.starred && !mail.labels.includes(3)) return false;
                if (flags.drafts && !mail.labels.includes(4)) return false;
                if (flags.spam && !mail.labels.includes(5)) return false;

                // Check if the mail matches all of the labels
                if (labels.length > 0 && !labels.every(label => mail.labels.includes(label))) {
                    return false;
                }

                return true;
            });
    } catch (error) {
        console.error('Search error:', error);
        return [];
    }
};

export { search };