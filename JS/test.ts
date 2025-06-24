private injectHookIntoProfileField(formFields: FormlyFieldConfig[]): FormlyFieldConfig[] {
  const updatedFields = [...formFields];
  const profileField = updatedFields.find(f => f.key === 'profilesList');

  if (profileField?.fieldArray) {
    // TypeScript ne reconnaît pas fieldGroup ici, on force le type
    const arrayConfig = profileField.fieldArray as FormlyFieldConfig;

    if (Array.isArray(arrayConfig.fieldGroup)) {
      const profileNameField = arrayConfig.fieldGroup.find(f => f.key === 'profileName');

      if (profileNameField) {
        profileNameField.hooks = {
          onInit: (field) => {
            field.formControl?.valueChanges.subscribe((selectedProfileName: string) => {
              const selected = this.listAvailableProfile.find(p => p.profileName === selectedProfileName);
              if (!selected) { return; }

              const formArray = this.form.get('profilesList');
              if (!(formArray instanceof FormArray)) { return; }

              const parentGroup = field.form?.parent;
              const index = formArray.controls.findIndex(ctrl => ctrl === parentGroup);
              if (index !== -1) {
                formArray.at(index).patchValue({
                  activation: selected.activation,
                  version: selected.version
                }, { emitEvent: false });
              }
            });
          }
        };
      }
    }
  }

  return updatedFields;
}
