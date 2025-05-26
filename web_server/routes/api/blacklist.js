import express from 'express';
import { addURL, deleteURL } from '../../controllers/blacklist.js';
const router = express.Router();

router.route('/')
        .post(addURL);
router.route('/:id')
        .delete(deleteURL);

export default router;