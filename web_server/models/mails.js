import { labels } from "./labels.js";

let mails = {};
let nextId = 1;

const createNewDraft = (from, to, cc, subject, body, attachments, userLabels = []) => {
    // Ensure 'to' is an array and remove duplicates
    const toUsers = Array.isArray(to) ? [...new Set(to)] : [to];
    const ccUsers = [...new Set(cc)];
    
    const newDraft = {
        from,
        to: toUsers,
        cc: ccUsers,
        subject,
        body,
        date: new Date(),
        read: true,
        attachments,
        labels: ['Drafts', ...userLabels]
    };
    const draftId = nextId++;
    
    // Add to sender's Drafts folder only
    mails[from] = mails[from] || {};
    mails[from][draftId] = JSON.parse(JSON.stringify(newDraft));
    
    // Update the mails field in numeric labels
    userLabels.filter(label => typeof label === 'number').forEach(labelId => {
        if (labels[from] && labels[from][labelId]) {
            if (!labels[from][labelId].mails.includes(draftId)) {
                labels[from][labelId].mails.push(draftId);
            }
        }
    });
    
    return draftId;
};

const createNewMail = (from, to, cc, subject, body, attachments, userLabels = []) => {
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
        labels: [...userLabels]
    };
    const mailId = nextId++;
    
    // Add to sender's Sent folder
    mails[from] = mails[from] || {};
    mails[from][mailId] = JSON.parse(JSON.stringify(newMail));
    mails[from][mailId].labels.push('Sent');
    
    // Update the mails field in numeric labels for sender
    userLabels.filter(label => typeof label === 'number').forEach(labelId => {
        if (labels[from] && labels[from][labelId]) {
            if (!labels[from][labelId].mails.includes(mailId)) {
                labels[from][labelId].mails.push(mailId);
            }
        }
    });
    
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
            
            // Update the mails field in numeric labels for recipient
            userLabels.filter(label => typeof label === 'number').forEach(labelId => {
                if (labels[user] && labels[user][labelId]) {
                    if (!labels[user][labelId].mails.includes(mailId)) {
                        labels[user][labelId].mails.push(mailId);
                    }
                }
            });
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
            
            // Update the mails field in numeric labels for CC recipient
            userLabels.filter(label => typeof label === 'number').forEach(labelId => {
                if (labels[user] && labels[user][labelId]) {
                    if (!labels[user][labelId].mails.includes(mailId)) {
                        labels[user][labelId].mails.push(mailId);
                    }
                }
            });
        }
    });
    
    return mailId;
};

const extractUrlsFromString = (text) => {
    if (!text || typeof text !== 'string') return [];
    const urlRegex = /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?/g;
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
    
    // Get the mail's labels before deleting
    const mail = mails[username][mailId];
    const mailLabels = mail.labels || [];
    
    // Remove this mail from all numeric labels' mails arrays
    mailLabels.filter(label => typeof label === 'number').forEach(labelId => {
        if (labels[username] && labels[username][labelId]) {
            const mailIndex = labels[username][labelId].mails.indexOf(parseInt(mailId));
            if (mailIndex > -1) {
                labels[username][labelId].mails.splice(mailIndex, 1);
            }
        }
    });
    
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
    // System labels are managed by the application logic, not user input
    const systemLabels = ['Sent', 'Inbox', 'Drafts'];
    const updatedMail = { ...mails[username][mailId] };
    
    // Track old labels before updating to manage label-mail relationships
    const oldLabels = updatedMail.labels || [];
    
    for (const field of allowedFields) {
        if (field in updates) {
            if (field === 'labels') {
                // Check if this is a spam-related update (contains 'Spam' or 'Inbox' manipulation)
                const hasSpamUpdate = updates[field].some(label => 
                    label === 'Spam' || label === 'Inbox'
                );
                
                if (hasSpamUpdate) {
                    // For spam updates, allow full control over labels
                    updatedMail[field] = updates[field];
                } else {
                    // For regular updates, preserve system labels and merge with new labels
                    const currentSystemLabels = (updatedMail[field] || []).filter(label => 
                        typeof label === 'string' && systemLabels.includes(label)
                    );
                    const newUserLabels = updates[field].filter(label => 
                        typeof label === 'number' || (typeof label === 'string' && !systemLabels.includes(label))
                    );
                    updatedMail[field] = [...currentSystemLabels, ...newUserLabels];
                }
                
                // Update the mails field in labels when labels are updated
                const newLabels = updatedMail[field] || [];
                
                // Remove mailId from old numeric labels that are no longer assigned
                const oldNumericLabels = oldLabels.filter(label => typeof label === 'number');
                const newNumericLabels = newLabels.filter(label => typeof label === 'number');
                
                oldNumericLabels.forEach(labelId => {
                    if (!newNumericLabels.includes(labelId) && labels[username] && labels[username][labelId]) {
                        // Remove mailId from this label's mails array
                        const mailIndex = labels[username][labelId].mails.indexOf(parseInt(mailId));
                        if (mailIndex > -1) {
                            labels[username][labelId].mails.splice(mailIndex, 1);
                        }
                    }
                });
                
                // Add mailId to new numeric labels that weren't previously assigned
                newNumericLabels.forEach(labelId => {
                    if (!oldNumericLabels.includes(labelId) && labels[username] && labels[username][labelId]) {
                        // Add mailId to this label's mails array if not already present
                        if (!labels[username][labelId].mails.includes(parseInt(mailId))) {
                            labels[username][labelId].mails.push(parseInt(mailId));
                        }
                    }
                });
            } else {
                updatedMail[field] = updates[field];
            }
        }
    }

    // Ensure drafts remain marked as read
    const isDraft = updatedMail.labels && updatedMail.labels.includes('Drafts');
    if (isDraft) {
        updatedMail.read = true;
    }

    mails[username][mailId] = updatedMail;
    return true;
};

const updateMailSpamStatus = (mailId, isSpam, reportingUsername) => {
    // Find all users who have this email
    const usersWithMail = [];
    for (const username in mails) {
        if (mails[username] && mails[username][mailId]) {
            usersWithMail.push(username);
        }
    }
    
    // Update the spam status for all users who have this email
    for (const username of usersWithMail) {
        const mail = mails[username][mailId];
        if (mail) {
            if (isSpam) {
                // Mark as spam: remove the inbox label and add spam label
                mail.labels = (mail.labels || []).filter(label => label !== 'Inbox' && label !== 'Sent');
                
                // Only add SPAM label to the reporting user 
                if (username === reportingUsername) {
                    mail.labels.push('Spam');
                }
            } else {
                // Unmark as spam: remove 'Spam' and add appropriate label based on user role
                mail.labels = (mail.labels || []).filter(label => label !== 'Spam');
                if (username === reportingUsername) {
                    // If the user is the reporting user, add 'Inbox' label
                    mail.labels.push('Inbox');
                } else {
                    // For other users, add 'Sent' label if it was sent by them
                    if (mail.from === username) {
                        mail.labels.push('Sent');
                    }
                }
            }
        }
    }
    
    return usersWithMail.length > 0;
};

export { mails, getLatestMails, createNewMail, createNewDraft, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail, updateMailSpamStatus };