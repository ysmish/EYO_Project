import { labels } from "./labels.js";

let mails = {
    'Mro': {
        1: {from: "Mro", to: "Evi", cc: [], subject: "Quick Update", body: "Hey Evi, just a quick update on the deployment. Everything went smoothly and the server is running perfectly!", date: new Date(), read: true, attachments: [], labels: ["Sent", "Inbox"]},
        2: {from: "Mro", to: "Evi", cc: [], subject: "Lunch Plans?", body: "Are you free for lunch today? I was thinking we could grab something and discuss the new features.", date: new Date(Date.now() - 2 * 60 * 60 * 1000), attachments: [], labels: ["Sent"]},
        3: {from: "Mro", to: "Evi", cc: [], subject: "Morning Coffee", body: "Good morning! Coffee is ready in the kitchen. Come grab a cup when you get a chance!", date: new Date(Date.now() - 4 * 60 * 60 * 1000), attachments: [], labels: ["Sent"]},
        4: {from: "Mro", to: "Evi", cc: [], subject: "Urgent: Server Issue", body: "We have a small issue with the authentication service. Can you take a look when you have a moment?", date: new Date(Date.now() - 1 * 60 * 60 * 1000), attachments: [], labels: ["Sent", 1, "Starred"]},
        5: {from: "Mro", to: "Evi", cc: [], subject: "Meeting Tomorrow", body: "Hey Evi, don't forget about our meeting tomorrow at 10 AM. We'll discuss the project timeline and deliverables.", date: new Date('2024-06-18T09:30:00'), attachments: [], labels: ["Sent", 2]},
        6: {from: "Mro", to: "Evi", cc: [], subject: "Code Review Required", body: "Hi Evi, I've pushed the latest changes to the main branch. Could you please review the authentication module when you have time?", date: new Date('2024-06-17T14:20:00'), attachments: [], labels: ["Sent"]},
        7: {from: "Mro", to: "Evi", cc: [], subject: "Weekend Plans", body: "Hey! Are you free this weekend? I was thinking we could work on the frontend components together. Let me know!", date: new Date('2024-01-13T16:45:00'), attachments: [], labels: ["Sent"]},
        8: {from: "Mro", to: "Evi", cc: [], subject: "Bug Fix Update", body: "I've fixed the login issue we discussed yesterday. The problem was with the token validation. Everything should work smoothly now.", date: new Date('2024-01-12T11:10:00'), attachments: [], labels: ["Sent", 2, "Starred"]},
        9: {from: "Mro", to: "Evi", cc: [], subject: "Database Schema Changes", body: "Hi Evi, I've made some changes to the database schema. Please update your local database with the new migration files.", date: new Date('2024-01-11T08:30:00'), attachments: [], labels: ["Sent"]},
        10: {from: "Mro", to: "Evi", cc: [], subject: "Great Job!", body: "Hey Evi, just wanted to say great job on the UI design. The new interface looks amazing and the user experience is much better now!", date: new Date('2024-01-10T17:25:00'), attachments: [], labels: ["Sent", "Starred"]},
        11: {from: "Mro", to: "Evi", cc: [], subject: "API Documentation", body: "I've updated the API documentation with the new endpoints. You can find it in the docs folder. Let me know if anything needs clarification.", date: new Date('2024-01-09T13:15:00'), attachments: [], labels: ["Sent"]},
        12: {from: "Mro", to: "Evi", cc: [], subject: "Test Results", body: "All unit tests are passing now! The test coverage is at 85%. We're making good progress on the quality assurance front.", date: new Date('2024-01-08T10:45:00'), attachments: [], labels: ["Sent", "Inbox"]}
    }
};
let nextId = 13;

const createNewMail = (from, to, cc, subject, body, attachments) => {
    const newMail = {
        from,
        to,
        cc,
        subject,
        body,
        date: new Date(),
        read: false,
        attachments,
        labels: []
    };
    const mailId = nextId++;
    mails[from] = mails[from] || {};
    mails[from][mailId] = JSON.parse(JSON.stringify(newMail));
    mails[from][mailId].labels.append('Sent'); // Automatically add to Sent label
    mails[to] = mails[to] || {};
    mails[to][mailId] = JSON.parse(JSON.stringify(newMail));
    mails[to][mailId].labels.append('Inbox'); // Automatically add to Inbox label
    cc.forEach(user => {
        mails[user] = mails[user] || {};
        mails[user][mailId] = JSON.parse(JSON.stringify(newMail));
        mails[user][mailId].labels.append('Inbox'); // Automatically add to Inbox label
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
    const disallowedLabels = ['Sent', 'Inbox', 'Starred', 'Drafts'];
    const updatedMail = { ...mails[username][mailId] };
    
    for (const field of allowedFields) {
        if (field in updates) {
            if (field === 'labels') {
                // Ensure labels do not include disallowed ones
                const newLabels = updates[field].filter(label => !disallowedLabels.includes(label));
                updatedMail[field] = newLabels;
            } else {
                updatedMail[field] = updates[field];
            }
        }
    }

    mails[username][mailId] = updatedMail;
    return true;
};

export { mails, getLatestMails, createNewMail, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail };