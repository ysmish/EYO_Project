import express from 'express';
import { getAllMails, getMailById, createMail, patchMail, deleteMail } from '../../controllers/mails.js';
const router = express.Router();

router.route('/')
        .get(getAllMails)
        .post(createMail);
router.route('/:id')
        .get(getMailById)
        .patch(patchMail)
        .delete(deleteMail);

export default router;