export function isValidEmail(email: string): boolean {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailRegex.test(email);
}

export function isValidPassword(password: string): boolean {
  const hasUpperCase = /[A-Z]+/.test(password);
  const hasLowerCase = /[a-z]+/.test(password);
  const hasNumeric = /[0-9]+/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  const isLengthValid = password.length >= 8;

  return hasUpperCase && hasLowerCase && hasNumeric && hasSpecialChar && isLengthValid;
}

export function isValidPhoneNumber(phone: string): boolean {
  const digitsOnly = phone.replace(/\D/g, '');
  return digitsOnly.length === 10;
}

export function isValidCreditCard(cardNumber: string): boolean {
  // Remove any non-digit characters
  const digitsOnly = cardNumber.replace(/\D/g, '');

  // Check if the length is valid (13-19 digits)
  if (digitsOnly.length < 13 || digitsOnly.length > 19) {
    return false;
  }

  // Luhn algorithm for credit card validation
  let sum = 0;
  let isEven = false;

  for (let i = digitsOnly.length - 1; i >= 0; i--) {
    let digit = parseInt(digitsOnly[i]);

    if (isEven) {
      digit *= 2;
      if (digit > 9) {
        digit -= 9;
      }
    }

    sum += digit;
    isEven = !isEven;
  }

  return sum % 10 === 0;
}

export function isValidZipCode(zipCode: string): boolean {
  const zipRegex = /^\d{5}(-\d{4})?$/;
  return zipRegex.test(zipCode);
}

export function isValidUrl(url: string): boolean {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
} 