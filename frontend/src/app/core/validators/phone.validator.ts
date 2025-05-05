import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function phoneValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null;
    }

    // Remove any non-digit characters
    const digitsOnly = value.replace(/\D/g, '');
    
    // Check if the number of digits is valid (10 for US numbers)
    const isValid = digitsOnly.length === 10;

    return isValid ? null : { invalidPhone: true };
  };
} 