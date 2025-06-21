import { labels } from "./labels.js";

let mails = {};
let nextId = 1;

const createNewMail = (from, to, cc, subject, body, attachments) => {
    // Ensure 'to' is an array and remove duplicates
    const toUsers = Array.isArray(to) ? [...new Set(to)] : [to];
    const ccUsers = [...new Set(cc)];
    
    const newMail = {
        from,
        to: toUsers,
        cc: ccUsers,
        subject,
        body,
        date: new Date(),
        read: false,
        attachments,
        labels: []
    };
    const mailId = nextId++;
    
    // Add to sender's Sent folder
    mails[from] = mails[from] || {};
    mails[from][mailId] = JSON.parse(JSON.stringify(newMail));
    mails[from][mailId].labels.push('Sent');
    
    // Add to each recipient's Inbox
    toUsers.forEach(user => {
        if (user === from) {
            // Self-sent mail: add 'Inbox' label to the existing mail (already has 'Sent')
            mails[from][mailId].labels.push('Inbox');
        } else {
            // Different recipient: create separate copy with 'Inbox' label
            mails[user] = mails[user] || {};
            mails[user][mailId] = JSON.parse(JSON.stringify(newMail));
            mails[user][mailId].labels.push('Inbox');
        }
    });
    
    // Add to each CC recipient's Inbox (only if not already in To list)
    ccUsers.forEach(user => {
        if (toUsers.includes(user)) {
            // User is already in To list, skip to avoid duplicates
            return;
        }
        
        if (user === from) {
            // Self-CC: add 'Inbox' label to the existing mail if not already added
            if (!mails[from][mailId].labels.includes('Inbox')) {
                mails[from][mailId].labels.push('Inbox');
            }
        } else {
            // Different CC recipient: create separate copy with 'Inbox' label
            mails[user] = mails[user] || {};
            mails[user][mailId] = JSON.parse(JSON.stringify(newMail));
            mails[user][mailId].labels.push('Inbox');
        }
    });
    
    return mailId;
};

const extractUrlsFromString = (text) => {
    if (!text || typeof text !== 'string') return [];
    const urlRegex = /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/g;
    return text.match(urlRegex) || [];
};

const extractUrlsFromMail = (mail) => {
    const urls = new Set();
    
    // Go through all fields in the mail object
    for (const [key, value] of Object.entries(mail)) {
        if (Array.isArray(value)) {
            // Handle arrays (like cc and attachments)
            value.forEach(item => {
                if (typeof item === 'string') {
                    extractUrlsFromString(item).forEach(url => urls.add(url));
                } else if (item && typeof item === 'object' && item.name) {
                    // Handle attachment objects with names
                    extractUrlsFromString(item.name).forEach(url => urls.add(url));
                }
            });
        } else if (typeof value === 'string') {
            // Handle string fields
            extractUrlsFromString(value).forEach(url => urls.add(url));
        }
    }
    
    return Array.from(urls);
};

const getMail = (username, mailId) => {
    if (mails[username] && mails[username][mailId]) {
        return { id: mailId, ...mails[username][mailId] };
    }
    return null;
};

const getLatestMails = (username, limit = 50) => {
    if (!mails[username]) {
        return [];
    }
    return Object.entries(mails[username])
        .map(([id, mail]) => ({ id: parseInt(id), ...mail }))
        .sort((a, b) => new Date(b.date) - new Date(a.date)).slice(0, limit);
};

const deleteMailOfUser = (username, mailId) => {
    if (!mails[username] || !mails[username][mailId]) {
        return false;
    }
    // Only delete the mail from the user's list
    delete mails[username][mailId];
    return true;
};

const updateMail = (username, mailId, updates) => {
    if (!mails[username] || !mails[username][mailId]) {
        return false;
    }

    // Only update allowed fields
    const allowedFields = ['subject', 'body', 'attachments', 'to', 'cc', 'read', 'labels'];
    const systemLabels = ['Sent', 'Inbox', 'Drafts'];
    const updatedMail = { ...mails[username][mailId] };
    
    for (const field of allowedFields) {
        if (field in updates) {
            if (field === 'labels') {
                // Preserve system labels and merge with new labels
                const currentSystemLabels = (updatedMail[field] || []).filter(label => 
                    typeof label === 'string' && systemLabels.includes(label)
                );
                const newUserLabels = updates[field].filter(label => 
                    typeof label === 'number' || (typeof label === 'string' && !systemLabels.includes(label))
                );
                updatedMail[field] = [...currentSystemLabels, ...newUserLabels];
            } else {
                updatedMail[field] = updates[field];
            }
        }
    }

    mails[username][mailId] = updatedMail;
    return true;
};

export { mails, getLatestMails, createNewMail, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail };