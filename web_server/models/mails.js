import Mail from '../service/Mail.js';
import Label from '../service/Label.js';

let nextId = 1;

const getNextId = async () => {
    const lastMail = await Mail.findOne().sort({ mailId: -1 });
    if (lastMail) {
        nextId = lastMail.mailId + 1;
    }
    return nextId++;
};

const createNewDraft = async (from, to, cc, subject, body, attachments, userLabels = []) => {
    try {
        // Ensure 'to' is an array and remove duplicates
        const toUsers = Array.isArray(to) ? [...new Set(to)] : [to];
        const ccUsers = [...new Set(cc)];
        
        const draftId = await getNextId();
        
        const newDraft = new Mail({
            mailId: draftId,
            from,
            to: toUsers,
            cc: ccUsers,
            subject,
            body,
            date: new Date(),
            read: true,
            attachments,
            labels: [4, ...userLabels],
            owner: from
        });
        
        await newDraft.save();
        
        // Update the mails field in numeric labels
        const numericLabels = userLabels.filter(label => typeof label === 'number');
        for (const labelId of numericLabels) {
            await Label.findOneAndUpdate(
                { username: from, id: labelId },
                { $push: { mails: newDraft._id } }
            );
        }
        
        return draftId;
    } catch (error) {
        console.error('Error creating draft:', error);
        throw error;
    }
};

const createNewMail = async (from, to, cc, subject, body, attachments, userLabels = []) => {
    try {
        // Ensure 'to' is an array and remove duplicates
        const toUsers = Array.isArray(to) ? [...new Set(to)] : [to];
        const ccUsers = [...new Set(cc)];
        
        const mailId = await getNextId();
        
        // Create mail for sender with 'Sent' label
        const senderMail = new Mail({
            mailId,
            from,
            to: toUsers,
            cc: ccUsers,
            subject,
            body,
            date: new Date(),
            read: false,
            attachments,
            labels: [2, ...userLabels],
            owner: from
        });
        
        await senderMail.save();
        
        // Update the mails field in numeric labels for sender
        const numericLabels = userLabels.filter(label => typeof label === 'number');
        for (const labelId of numericLabels) {
            await Label.findOneAndUpdate(
                { username: from, id: labelId },
                { $push: { mails: senderMail._id } }
            );
        }
        
        // Add to each recipient's Inbox
        for (const user of toUsers) {
            if (user === from) {
                // Self-sent mail: add 'Inbox' label to the existing mail
                senderMail.labels.push(1);
                await senderMail.save();
            } else {
                // Different recipient: create separate copy with 'Inbox' label
                const recipientMail = new Mail({
                    mailId,
                    from,
                    to: toUsers,
                    cc: ccUsers,
                    subject,
                    body,
                    date: new Date(),
                    read: false,
                    attachments,
                    labels: [1, ...userLabels],
                    owner: user
                });
                
                await recipientMail.save();
                
                // Update the mails field in numeric labels for recipient
                for (const labelId of numericLabels) {
                    await Label.findOneAndUpdate(
                        { username: user, id: labelId },
                        { $push: { mails: recipientMail._id } }
                    );
                }
            }
        }
        
        // Add to each CC recipient's Inbox (only if not already in To list)
        for (const user of ccUsers) {
            if (toUsers.includes(user)) {
                // User is already in To list, skip to avoid duplicates
                continue;
            }
            
            if (user === from) {
                // Self-CC: add 'Inbox' label to the existing mail (already has 'Sent')
                if (!senderMail.labels.includes(1)) {
                    senderMail.labels.push(1);
                    await senderMail.save();
                }
            } else {
                // Different CC recipient: create separate copy with 'Inbox' label
                const ccMail = new Mail({
                    mailId,
                    from,
                    to: toUsers,
                    cc: ccUsers,
                    subject,
                    body,
                    date: new Date(),
                    read: false,
                    attachments,
                    labels: [1, ...userLabels],
                    owner: user
                });
                
                await ccMail.save();
                
                // Update the mails field in numeric labels for CC recipient
                for (const labelId of numericLabels) {
                    await Label.findOneAndUpdate(
                        { username: user, id: labelId },
                        { $push: { mails: ccMail._id } }
                    );
                }
            }
        }
        
        return mailId;
    } catch (error) {
        console.error('Error creating mail:', error);
        throw error;
    }
};

const getMails = async (username) => {
    try {
        const mails = await Mail.find({ owner: username }).sort({ date: -1 });
        return mails.reduce((acc, mail) => {
            acc[mail.mailId] = {
                from: mail.from,
                to: mail.to,
                cc: mail.cc,
                subject: mail.subject,
                body: mail.body,
                date: mail.date,
                read: mail.read,
                attachments: mail.attachments,
                labels: mail.labels
            };
            return acc;
        }, {});
    } catch (error) {
        console.error('Error fetching mails:', error);
        return {};
    }
};

const getMail = async (username, mailId) => {
    try {
        const mail = await Mail.findOne({ owner: username, mailId });
        if (!mail) {
            return null;
        }
        console.error('Mail found:', mail);
        return {
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
        };
    } catch (error) {
        console.error('Error fetching mail:', error);
        return null;
    }
};

const updateMail = async (username, mailId, updates) => {
    try {
        const mail = await Mail.findOne({ owner: username, mailId });
        if (!mail) {
            return null;
        }
        
        // Only update allowed fields
        const allowedFields = ['subject', 'body', 'attachments', 'to', 'cc', 'read', 'labels'];
        // System labels are managed by the application logic, not user input
        const systemLabels = [1, 2, 4];
        
        // Track old labels before updating to manage label-mail relationships
        const oldLabels = mail.labels || [];
        
        for (const field of allowedFields) {
            if (field in updates) {
                if (field === 'labels') {
                    // Check if this is a spam-related update (contains 'Spam' or 'Inbox' manipulation)
                    const hasSpamUpdate = updates[field].some(label => 
                        label === 5 || label === 1
                    );
                    
                    if (hasSpamUpdate) {
                        // For spam updates, allow full control over labels
                        mail[field] = updates[field];
                    } else {
                        // For regular updates, preserve system labels and merge with new labels
                        const currentSystemLabels = (mail[field] || []).filter(label => 
                            typeof label === 'string' && systemLabels.includes(label)
                        );
                        const newUserLabels = updates[field].filter(label => 
                            typeof label === 'number' || (typeof label === 'string' && !systemLabels.includes(label))
                        );
                        mail[field] = [...currentSystemLabels, ...newUserLabels];
                    }
                    
                    // Update the mails field in labels when labels are updated
                    const newLabels = mail[field] || [];
                    
                    // Remove mailId from old numeric labels that are no longer assigned
                    const oldNumericLabels = oldLabels.filter(label => typeof label === 'number');
                    const newNumericLabels = newLabels.filter(label => typeof label === 'number');
                    
                    for (const labelId of oldNumericLabels) {
                        if (!newNumericLabels.includes(labelId)) {
                            // Remove mailId from this label's mails array
                            await Label.findOneAndUpdate(
                                { username: username, id: labelId },
                                { $pull: { mails: mail._id } }
                            );
                        }
                    }
                    
                    // Add mailId to new numeric labels that weren't previously assigned
                    for (const labelId of newNumericLabels) {
                        if (!oldNumericLabels.includes(labelId)) {
                            // Add mailId to this label's mails array if not already present
                            await Label.findOneAndUpdate(
                                { username: username, id: labelId },
                                { $addToSet: { mails: mail._id } }
                            );
                        }
                    }
                } else {
                    mail[field] = updates[field];
                }
            }
        }
        
        // Ensure drafts remain marked as read
        const isDraft = mail.labels && mail.labels.includes(4);
        if (isDraft) {
            mail.read = true;
        }
        
        await mail.save();
        
        return {
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
        };
    } catch (error) {
        console.error('Error updating mail:', error);
        return null;
    }
};

const updateMailSpamStatus = async (mailId, isSpam, reportingUsername) => {
    try {
        // Find all users who have this email
        const usersWithMail = await Mail.find({ mailId });
        
        if (usersWithMail.length === 0) {
            return false;
        }
        
        // Update the spam status for all users who have this email
        for (const mail of usersWithMail) {
            const username = mail.owner;
            
            if (isSpam) {
                // Mark as spam: remove the inbox label and add spam label
                mail.labels = (mail.labels || []).filter(label => label !== 1 && label !== 2);
                
                mail.labels.push(5); // Add 'Spam' label
            } else {
                // Unmark as spam: remove 'Spam' and add appropriate label based on user role
                mail.labels = (mail.labels || []).filter(label => label !== 5);
                if (username === reportingUsername) {
                    // If the user is the reporting user, add 'Inbox' label
                    mail.labels.push(1);
                } else {
                    // For other users, add 'Sent' label if it was sent by them
                    if (mail.from === username) {
                        mail.labels.push(2);
                    }
                }
            }
            
            await mail.save();
        }
        
        return true;
    } catch (error) {
        console.error('Error updating mail spam status:', error);
        return false;
    }
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

const deleteMail = async (username, mailId) => {
    try {
        const mail = await Mail.findOne({ owner: username, mailId });
        if (!mail) {
            return false;
        }
        
        // Get the mail's labels before deleting
        const mailLabels = mail.labels || [];
        
        // Remove this mail from all numeric labels' mails arrays
        const numericLabels = mailLabels.filter(label => typeof label === 'number');
        for (const labelId of numericLabels) {
            await Label.findOneAndUpdate(
                { username: username, id: labelId },
                { $pull: { mails: mail._id } }
            );
        }
        
        // Delete the mail
        const result = await Mail.deleteOne({ owner: username, mailId });
        return result.deletedCount > 0;
    } catch (error) {
        console.error('Error deleting mail:', error);
        return false;
    }
};

export { createNewDraft, createNewMail, getMails, getMail, updateMail, deleteMail, extractUrlsFromMail, updateMailSpamStatus };
