document.addEventListener('DOMContentLoaded', () => {
    const themeToggleBtn = document.querySelector('.js-theme-toggle');
    const htmlTag = document.documentElement;
    const savedTheme = localStorage.getItem('aem-theme') || 'light';
    htmlTag.dataset.theme = savedTheme;

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = htmlTag.dataset.theme;
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            
            htmlTag.dataset.theme = newTheme;
            
            localStorage.setItem('aem-theme', newTheme);
        });
    }
});
