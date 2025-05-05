import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null;
    }

    const hasUpperCase = /[A-Z]+/.test(value);
    const hasLowerCase = /[a-z]+/.test(value);
    const hasNumeric = /[0-9]+/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    const isLengthValid = value.length >= 8;

    const errors: ValidationErrors = {};

    if (!hasUpperCase) {
      errors['hasUpperCase'] = true;
    }
    if (!hasLowerCase) {
      errors['hasLowerCase'] = true;
    }
    if (!hasNumeric) {
      errors['hasNumeric'] = true;
    }
    if (!hasSpecialChar) {
      errors['hasSpecialChar'] = true;
    }
    if (!isLengthValid) {
      errors['minLength'] = true;
    }

    return Object.keys(errors).length > 0 ? errors : null;
  };
}

 