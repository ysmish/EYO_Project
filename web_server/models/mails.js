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
// TODO: delete comment when mails are implemented
let mails = []

const getLatestMails = (limit = 50) => {
    return [...mails]
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, limit);
};

export { mails, getLatestMails };