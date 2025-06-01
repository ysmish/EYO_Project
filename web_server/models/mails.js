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

const extractUrls = (text) => {
    const urlRegex = /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/g;
    return text.match(urlRegex) || [];
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


export { mails, getLatestMails, createNewMail, extractUrls, getMail };