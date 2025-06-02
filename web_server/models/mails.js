let mails = {};
let nextId = 1;

const createNewMail = (from, to, cc, subject, body, attachments) => {
    const newMail = {
        from,
        to,
        cc,
        subject,
        body,
        date: new Date(),
        attachments
    };
    const mailId = nextId++;
    mails[from] = mails[from] || {};
    mails[from][mailId] = newMail;
    mails[to] = mails[to] || {};
    mails[to][mailId] = newMail;
    cc.forEach(user => {
        mails[user] = mails[user] || {};
        mails[user][mailId] = newMail;
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

export { mails, getLatestMails, createNewMail, extractUrlsFromMail, getMail, deleteMailOfUser };