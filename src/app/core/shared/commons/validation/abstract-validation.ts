import { ValidationErrors, ValidatorFn, Validators } from "@angular/forms";

export abstract class AbstractValidation {

    static email(): Validators[]{
        return [Validators.email, Validators.required, Validators.minLength(5)]
    }

    static strongPassword(): Validators[] {
        return [Validators.pattern("?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}")];
    }
}