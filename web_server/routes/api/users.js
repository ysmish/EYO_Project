import express from 'express';
import { createNewUser, getUserById, changeUserPhoto } from '../../controllers/users.js';
const router = express.Router();

router.route('/')
        .post(createNewUser);
router.route('/:id')
        .get(getUserById);
router.route('/photo')
        .post(changeUserPhoto);

export default router;