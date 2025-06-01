/**
 * [
    {
        "id": 1,
        "from": "user1",
        "to": "user2",
        "cc": ["user3"],
        "subject": "Hello",
        "body": "This is a test email.",
        "date": new Date("2023-10-01T10:00:00Z"),
        "read": false,
        "attachments": []
    }
]
 */
// ***IF CHAGING STRUCTURE, UPDATE THE SEARCH FUNCTION IN search.js***
let mails = []

const getMailById = (mailId) => {
    const mail = mails.find(m => m.id === mailId);
    if (!mail) {
        throw new Error('Mail not found');
    }
    return mail;
};

const hasAccessToMail = (mail, username) => {
    return mail.from === username || 
           mail.to === username || 
           (mail.cc && mail.cc.includes(username));
};

const getLatestMails = (username, limit = 50) => {
    return [...mails]
        .filter(mail => 
            mail.from === username || 
            mail.to === username || 
            (mail.cc && mail.cc.includes(username))
        )
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, limit);
};

export { mails, getLatestMails, getMailById, hasAccessToMail };
