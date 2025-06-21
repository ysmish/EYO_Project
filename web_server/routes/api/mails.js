import express from 'express';
import { getAllMails, getMailById, createMail, createDraft, sendDraft, patchMail, deleteMail } from '../../controllers/mails.js';
const router = express.Router();

router.route('/')
        .get(getAllMails)
        .post(createMail);
router.route('/drafts')
        .post(createDraft);
router.route('/drafts/:id/send')
        .post(sendDraft);
router.route('/:id')
        .get(getMailById)
        .patch(patchMail)
        .delete(deleteMail);

export default router;