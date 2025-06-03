import { getLatestMails, createNewMail, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail } from '../models/mails.js';
import { checkUrl } from '../models/blacklist.js';
import { getUser } from '../models/users.js';

const getAllMails = (req, res) => {
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required.' });
    }
    try {
        const latestMails = getLatestMails(username);
        return res.status(200).json(latestMails);
    } catch (error) {
        console.error('Error fetching latest mails:', error);
        return res.status(500).json({error: 'Internal server error.' });
    }
}

const getMailById = (req, res) => {
    const username = req.headers.authorization;
    
    // Check if user is authenticated
    if (!username) {
        return res.status(400).json({error: 'Username is required.'});
    }

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
        // Get username from header
        const username = req.headers.authorization;
        if (!username) {
            return res.status(400).json({error: 'Username is required' });
        }

        const { to, subject, body, attachments } = req.body;
        const cc = req.body.cc || [];

        // Check if users exist
        if (getUser(username).error || getUser(to).error) {
            return res.status(400).json({error: 'User not found' });
        }
        for (const user of cc) {
            if (getUser(user).error) {
                return res.status(400).json({error: 'User not found' });
            }
        }

        // Basic validation
        if (!to || !subject || !body) {
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

        // Create the mail if all URLs are valid
        const mailId = createNewMail(
            username,
            to,
            cc,
            subject,
            body,
            attachments || []
        );

        return res.status(201).location(`/api/mails/${mailId}`).end();
}


const patchMail = async (req, res) => {
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required.' });
    }

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
    
    // Extract URLs from the updates to check against blacklist
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

    // Update the mail
    const success = updateMail(username, mailId, updates);
    if (success) {
        return res.status(204).location(`/api/mails/${mailId}`).end();
    } else {
        return res.status(500).json({ error: 'Failed to update mail' });
    }
};

const deleteMail = (req, res) => {
    // Get username from header
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required.' });
    }

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

export { getAllMails, getMailById, createMail, patchMail, deleteMail }; 