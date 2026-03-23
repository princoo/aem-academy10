document.addEventListener('DOMContentLoaded', () => {
    const toggleBtn = document.querySelector('.js-header-toggle');
    const closeBtn = document.querySelector('.js-header-close');
    const modal = document.querySelector('.js-header-modal');
    const body = document.body;

    if (!toggleBtn || !closeBtn || !modal) {return;} 
    const openModal = () => {
        toggleBtn.classList.add('hide');
        closeBtn.classList.remove('hide');
        modal.classList.add('is-open');
        body.classList.add('modal-open');
    };

    const closeModal = () => {
        toggleBtn.classList.remove('hide');
        closeBtn.classList.add('hide');
        modal.classList.remove('is-open');
        body.classList.remove('modal-open');
    };

    toggleBtn.addEventListener('click', openModal);
    closeBtn.addEventListener('click', closeModal);
});