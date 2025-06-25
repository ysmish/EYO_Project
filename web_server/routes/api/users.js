import express from 'express';
import multer from 'multer';
import { createNewUser, getUserById, changeUserPhoto } from '../../controllers/users.js';

const router = express.Router();
const upload = multer({ storage: multer.memoryStorage() }); // Store file in memory

router.route('/')
        .post(createNewUser);
router.route('/:id')
        .get(getUserById);
router.route('/photo')
        .post(upload.single('photo'), changeUserPhoto); // Use multer middleware

export default router;