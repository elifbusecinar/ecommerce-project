export const ERROR_MESSAGES = {
  REQUIRED: 'This field is required',
  EMAIL: {
    INVALID: 'Please enter a valid email address'
  },
  PASSWORD: {
    UPPERCASE: 'Password must contain at least one uppercase letter',
    LOWERCASE: 'Password must contain at least one lowercase letter',
    NUMBER: 'Password must contain at least one number',
    SPECIAL: 'Password must contain at least one special character',
    LENGTH: 'Password must be at least 8 characters long'
  },
  PHONE: {
    INVALID: 'Please enter a valid phone number'
  },
  AUTH: {
    INVALID_CREDENTIALS: 'Invalid email or password',
    EMAIL_EXISTS: 'Email already exists',
    UNAUTHORIZED: 'You are not authorized to perform this action'
  },
  CART: {
    ADD_ERROR: 'Failed to add item to cart',
    UPDATE_ERROR: 'Failed to update cart',
    REMOVE_ERROR: 'Failed to remove item from cart'
  },
  ORDER: {
    CREATE_ERROR: 'Failed to create order',
    UPDATE_ERROR: 'Failed to update order',
    CANCEL_ERROR: 'Failed to cancel order',
    NOT_FOUND: 'Order not found'
  },
  PRODUCT: {
    NOT_FOUND: 'Product not found',
    OUT_OF_STOCK: 'Product is out of stock'
  },
  PAYMENT: {
    FAILED: 'Payment failed',
    INVALID_CARD: 'Invalid card details'
  },
  NETWORK: {
    ERROR: 'Network error occurred. Please try again.',
    TIMEOUT: 'Request timed out. Please try again.'
  }
}; 