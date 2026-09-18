document.querySelectorAll('.error').forEach((el) => {
  if (el.textContent.trim().length > 0) {
    el.setAttribute('role', 'alert');
  }
});
