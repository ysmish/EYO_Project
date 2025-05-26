import express from 'express';
import { createNewUser, getUserById } from '../../controllers/users.js';
const router = express.Router();

router.route('/')
        .post(createNewUser);
router.route('/:id')
        .get(getUserById);

export default router;