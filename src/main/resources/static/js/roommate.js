document.querySelectorAll('form[data-time-range]').forEach(form => {
  const start = form.querySelector('[data-start]');
  const end = form.querySelector('[data-end]');
  const validate = () => end.setCustomValidity(start.value && end.value && end.value <= start.value ? 'End time must be after start time.' : '');
  start.addEventListener('input', validate);
  end.addEventListener('input', validate);
  validate();
});
document.querySelectorAll('form[data-confirm]').forEach(form => {
  form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});
