import { getLatestMails, createNewMail, createNewDraft, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail, updateMailSpamStatus } from '../models/mails.js';
import { checkUrl, addUrl, deleteUrl } from '../models/blacklist.js';
import { getUser } from '../models/users.js';
import { authorizeToken } from '../models/tokens.js';

const getAllMails = (req, res) => {
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    try {
        const latestMails = getLatestMails(username);
        return res.status(200).json(latestMails);
    } catch (error) {
        console.error('Error fetching latest mails:', error);
        return res.status(500).json({error: 'Internal server error.' });
    }
}

const getMailById = (req, res) => {
    const token = req.headers.authorization;
    
    // Check if user is authenticated
    if (!token) {
        return res.status(401).json({error: 'Authorization token is required.'});
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({error: 'Invalid mail ID'});
    }

    const mail = getMail(username, mailId);
        if (!mail) {
            return res.status(404).json({error: 'Mail not found'});
        }
        else {
            return res.status(200).json(mail);
        }
}

const createMail = async (req, res) => {
        // Get token from header
        const token = req.headers.authorization;
        if (!token) {
            return res.status(401).json({error: 'Authorization token is required' });
        }

        // Verify JWT token
        const authResult = authorizeToken(token);
        if (authResult.error) {
            return res.status(401).json({ error: authResult.error });
        }

        const username = authResult.username;

        const { to, subject, body, attachments } = req.body;
        const cc = req.body.cc || [];

        // Ensure 'to' is an array
        const toUsers = Array.isArray(to) ? to : [to];
        
        // Check if users exist
        const senderCheck = getUser(username);
        if (senderCheck.error) {
            return res.status(400).json({error: 'Sender user not found' });
        }
        
        for (const user of toUsers) {
            const userCheck = getUser(user);
            if (userCheck.error) {
                return res.status(400).json({error: `User '${user}' not found` });
            }
        }
        
        for (const user of cc) {
            const userCheck = getUser(user);
            if (userCheck.error) {
                return res.status(400).json({error: `User '${user}' not found` });
            }
        }

        // Basic validation
        if (!toUsers.length || !subject || !body) {
            return res.status(400).json({error: 'Missing required fields' });
        }

        // Extract URLs from all mail fields
        try {
            const urls = extractUrlsFromMail({
                from: username,
                to,
                cc,
                subject,
                body,
                attachments
            });

            // Check all URLs against the URL server
            let hasBlacklistedUrls = false;
            for (const url of urls) {
                const isBlacklisted = await checkUrl(url);
                if (isBlacklisted) {
                    hasBlacklistedUrls = true;
                    break; // Found at least one blacklisted URL
                }
            }

            // If blacklisted URLs found, create mail and tag as SPAM for recipients
            if (hasBlacklistedUrls) {
                const mailId = createNewMail(
                    username,
                    toUsers,
                    cc,
                    subject,
                    body,
                    attachments || []
                );
                
                // Tag as SPAM for all recipients (but not for sender)
                updateMailSpamStatus(mailId, true, username);
                
                return res.status(201).location(`/api/mails/${mailId}`).end();
            }
        } catch (error) {
            console.error('Error checking URLs:', error);
            return res.status(500).json({ error: 'Failed to validate URLs' });
        }

        // Create the mail if all URLs are valid (no blacklisted URLs found)
        const mailId = createNewMail(
            username,
            toUsers,
            cc,
            subject,
            body,
            attachments || []
        );

        return res.status(201).location(`/api/mails/${mailId}`).end();
}

const createDraft = async (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({error: 'Authorization token is required' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    const { to, subject, body, attachments } = req.body;
    const cc = req.body.cc || [];

    // Ensure 'to' is an array
    const toUsers = Array.isArray(to) ? to : [to];
    
    // Check if sender exists
    const senderCheck = getUser(username);
    if (senderCheck.error) {
        return res.status(400).json({error: 'Sender user not found' });
    }
    
    // For drafts, we don't need to validate if recipients exist since they're not sent yet
    // We also don't need to check for blacklisted URLs since drafts are not sent

    // Create the draft
    const draftId = createNewDraft(
        username,
        toUsers,
        cc,
        subject || '',
        body || '',
        attachments || []
    );

    return res.status(201).json({ id: draftId, message: 'Draft saved successfully' });
};

const sendDraft = async (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({error: 'Authorization token is required' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;
    const draftId = parseInt(req.params.id);
    
    if (isNaN(draftId)) {
        return res.status(400).json({ error: 'Invalid draft ID' });
    }

    // Get the draft
    const draft = getMail(username, draftId);
    if (!draft) {
        return res.status(404).json({ error: 'Draft not found' });
    }

    // Verify it's actually a draft
    if (!draft.labels || !draft.labels.includes('Drafts')) {
        return res.status(400).json({ error: 'Mail is not a draft' });
    }

    // Get updated data from request body (in case user modified the draft)
    const { to, subject, body, attachments } = req.body;
    const cc = req.body.cc || [];

    // Use updated data or fall back to draft data
    const finalTo = to !== undefined ? (Array.isArray(to) ? to : [to]) : draft.to;
    const finalCc = cc !== undefined ? cc : draft.cc;
    const finalSubject = subject !== undefined ? subject : draft.subject;
    const finalBody = body !== undefined ? body : draft.body;
    const finalAttachments = attachments !== undefined ? attachments : draft.attachments;

    // Validate required fields
    if (!finalTo.length || !finalSubject || !finalBody) {
        return res.status(400).json({error: 'Missing required fields' });
    }

    // Check if users exist
    for (const user of finalTo) {
        const userCheck = getUser(user);
        if (userCheck.error) {
            return res.status(400).json({error: `User '${user}' not found` });
        }
    }
    
    for (const user of finalCc) {
        const userCheck = getUser(user);
        if (userCheck.error) {
            return res.status(400).json({error: `User '${user}' not found` });
        }
    }

    // Extract URLs and check blacklist
    try {
        const urls = extractUrlsFromMail({
            from: username,
            to: finalTo,
            cc: finalCc,
            subject: finalSubject,
            body: finalBody,
            attachments: finalAttachments
        });

        // Check all URLs against the URL server
        let hasBlacklistedUrls = false;
        for (const url of urls) {
            const isBlacklisted = await checkUrl(url);
            if (isBlacklisted) {
                hasBlacklistedUrls = true;
                break; // Found at least one blacklisted URL
            }
        }

        // If blacklisted URLs found, create mail and tag as SPAM for recipients
        if (hasBlacklistedUrls) {
            // Delete the draft first
            const deleteSuccess = deleteMailOfUser(username, draftId);
            if (!deleteSuccess) {
                return res.status(500).json({ error: 'Failed to delete draft' });
            }

            // Create the mail with blacklisted URLs
            const mailId = createNewMail(
                username,
                finalTo,
                finalCc,
                finalSubject,
                finalBody,
                finalAttachments
            );
            
            // Tag as SPAM for all recipients (but not for sender)
            updateMailSpamStatus(mailId, true, username);
            
            return res.status(201).json({ 
                id: mailId, 
                message: 'Draft sent successfully (marked as spam due to blacklisted URLs)' 
            });
        }
    } catch (error) {
        console.error('Error checking URLs:', error);
        return res.status(500).json({ error: 'Failed to validate URLs' });
    }

    // Delete the draft first
    const deleteSuccess = deleteMailOfUser(username, draftId);
    if (!deleteSuccess) {
        return res.status(500).json({ error: 'Failed to delete draft' });
    }

    // Create the mail (URLs are valid)
    try {
        const mailId = createNewMail(
            username,
            finalTo,
            finalCc,
            finalSubject,
            finalBody,
            finalAttachments
        );

        return res.status(201).json({ 
            id: mailId, 
            message: 'Draft sent successfully' 
        });
    } catch (error) {
        console.error('Error creating mail after deleting draft:', error);
        
        // Try to recreate the draft since we deleted it but couldn't send
        try {
            createNewDraft(
                username,
                finalTo,
                finalCc,
                finalSubject,
                finalBody,
                finalAttachments
            );
            return res.status(500).json({ 
                error: 'Failed to send mail, draft has been restored' 
            });
        } catch (restoreError) {
            return res.status(500).json({ 
                error: 'Failed to send mail and could not restore draft' 
            });
        }
    }
};

const patchMail = async (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    // Check if the mail exists and belongs to the user
    const mail = getMail(username, mailId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const updates = req.body;
    
    // Handle spam/not spam reporting
    if (updates.reportSpam !== undefined) {
        try {
            const urls = extractUrlsFromMail(mail);
            
            if (updates.reportSpam) {
                // Report as spam: add URLs to blacklist
                for (const url of urls) {
                    await addUrl(url);
                }
                // Update spam status for ALL users who have this email
                updateMailSpamStatus(mailId, true, username);
            } else {
                // Report as not spam: remove URLs from blacklist
                for (const url of urls) {
                    await deleteUrl(url);
                }
                // Update spam status for ALL users who have this email
                updateMailSpamStatus(mailId, false, username);
            }
            
            // Remove reportSpam from updates since it's not a mail field
            delete updates.reportSpam;
            
            // Return success immediately since we've handled the spam update globally
            return res.status(204).end();
        } catch (error) {
            console.error('Error handling spam report:', error);
            return res.status(500).json({ error: 'Failed to process spam report' });
        }
    } else {
        // Regular mail update - check URLs against blacklist
        try {
            const urls = extractUrlsFromMail(updates);
            
            // Check all URLs against the URL server
            for (const url of urls) {
                const isBlacklisted = await checkUrl(url);
                if (isBlacklisted) {
                    return res.status(400).json({ 
                        error: `URL ${url} is blacklisted`,
                        message: 'Found blacklisted URL in mail content'
                    });
                }
            }
        } catch (error) {
            console.error('Error checking URLs:', error);
            return res.status(500).json({ error: 'Failed to validate URLs' });
        }
    }

    // Update the mail
    const success = updateMail(username, mailId, updates);
    if (success) {
        return res.status(204).end();
    } else {
        return res.status(500).json({ error: 'Failed to update mail' });
    }
};

const deleteMail = (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    // Check if the mail exists and belongs to the user
    const mail = getMail(username, mailId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    // Delete the mail
    const success = deleteMailOfUser(username, mailId);
    if (success) {
        return res.status(204).end();
    } else {
        return res.status(500).json({ error: 'Failed to delete mail' });
    }
}

export { getAllMails, getMailById, createMail, createDraft, sendDraft, patchMail, deleteMail }; 