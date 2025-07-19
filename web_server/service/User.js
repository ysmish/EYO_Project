import mongoose from 'mongoose';

const userSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true,
    unique: true,
    minlength: 3,
    match: /^[a-zA-Z0-9]+$/
  },
  firstName: {
    type: String,
    required: true,
    match: /^[a-zA-Z0-9]+$/
  },
  lastName: {
    type: String,
    required: true,
    match: /^[a-zA-Z0-9]+$/
  },
  birthday: {
    type: Date,
    required: true,
    validate: {
      validator: function(v) {
        return v <= new Date();
      },
      message: 'Birthday must be in the past'
    }
  },
  email: {
    type: String,
    required: true,
    unique: true
  },
  password: {
    type: String,
    required: true,
    minlength: 6
  },
  photo: {
    type: String,
    default: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxjaXJjbGUgY3g9IjUwIiBjeT0iMzUiIHI9IjE1IiBmaWxsPSIjOUNBM0FGIi8+CjxwYXRoIGQ9Ik0yMCA4MEM3NS4wNzY5IDc5LjIzMDggMjQgNzUuMDc2OSAyNCA3MEMyNCA2NC45MjMxIDc1LjA3NjkgNjAgODAgNjAiIGZpbGw9IiM5Q0EzQUYiLz4KPC9zdmc+'
  }
}, {
  timestamps: true
});

const User = mongoose.model('User', userSchema);

export default User;
