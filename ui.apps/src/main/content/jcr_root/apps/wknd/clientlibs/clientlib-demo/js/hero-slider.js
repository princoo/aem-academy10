(function() {
    'use strict';
    function initSwipers() {
        const bannerElements = document.querySelectorAll('.hero-banner__swiper');

        bannerElements.forEach(function(bannerElement) {
            if (!bannerElement.swiper) {
                const parentBanner = bannerElement.closest('.hero-banner');
                const isEditMode = parentBanner?.classList.contains('is-edit-mode');
                const speedVal = Number.parseInt(bannerElement.dataset.speed, 10);
                const autoplayConfig = isEditMode ? false : {
                delay: speedVal,
                disableOnInteraction: false,
                };
                new Swiper(bannerElement, {
                    direction: 'horizontal', 
                    loop: true,
                    autoplay: autoplayConfig,
                    pagination: {
                        el: '.swiper-pagination',
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

    // 2. Run it once on initial page load
    document.addEventListener('DOMContentLoaded', initSwipers);

    // 3. The AEM Magic: Set up a MutationObserver to watch for dynamically added components
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            // If AEM added new HTML nodes to the page...
            if (mutation.addedNodes && mutation.addedNodes.length > 0) {
                // ...check if any of them are our sliders and initialize them!
                initSwipers();
            }
        });
    });

    // Start watching the entire body for changes
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });

})();