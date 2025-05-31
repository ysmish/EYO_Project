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
        "attachments": []
    }
]
 */
// ***IF CHAGING STRUCTURE, UPDATE THE SEARCH FUNCTION IN search.js***

let mails = [];
let nextId = 1;

const createNewMail = (from, to, cc, subject, body, attachments) => {
    const newMail = {
        id: nextId++,
        from,
        to,
        cc,
        subject,
        body,
        date: new Date(),
        attachments
    };
    mails.push(newMail);
    console.log(newMail);
    return newMail;
};

const extractUrls = (text) => {
    const urlRegex = /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/g;
    return text.match(urlRegex) || [];
};

export { mails, createNewMail, extractUrls };