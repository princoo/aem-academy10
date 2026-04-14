(function() {
    'use strict';
    function initSwipers() {
        const bannerElements = document.querySelectorAll('.hero-banner__swiper');
        const savedIndex = bannerElements[0]?.dataset.initialSlide || 0;
        bannerElements.forEach(function(bannerElement) {
            if (!bannerElement.swiper) {
                const parentBanner = bannerElement.closest('.hero-banner');
                const isEditMode = parentBanner?.classList.contains('is-edit-mode');
                const finalStartIndex = isEditMode ? savedIndex : 0;
                const speedVal = Number.parseInt(bannerElement.dataset.speed, 10);
                const autoplayConfig = isEditMode ? false : {
                delay: speedVal,
                disableOnInteraction: false,
                };
                new Swiper(bannerElement, {
                    direction: 'horizontal', 
                    loop: true,
                    autoplay: autoplayConfig,
                    initialSlide: Number.parseInt(finalStartIndex),
                    pagination: {
                        el: '.js-banner-pagination',
                        clickable: true 
                    },
                    navigation: {
                        nextEl: '.js-banner-next',
                        prevEl: '.js-banner-prev',
                    }
                });
                console.log("swiper initialized");
            }
        });
    }

    document.addEventListener('DOMContentLoaded', initSwipers);

    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.addedNodes && mutation.addedNodes.length > 0) {
                initSwipers();
            }
        });
    });

    observer.observe(document.body, {
        childList: true,
        subtree: true
    });
})();