(function() {
    'use strict';

    function initCourseSlider() {
        const sliders = document.querySelectorAll('.course-slider');

        sliders.forEach(function(slider) {
            if (!slider.swiper) {
                new Swiper(slider, {
                    slidesPerView: 1,
                    spaceBetween: 24,
                    navigation: {
                        nextEl: '.course-slider__next',
                        prevEl: '.course-slider__prev',
                    },
                    pagination: {
                        el: '.course-slider__pagination',
                        clickable: true
                    },
                    breakpoints: {
                        450: {
                            slidesPerView: 2,
                        },
                        800:{
                            slidesPerView: 3,
                        },
                        1024: {
                            slidesPerView: 4,
                        },
                        1400: {
                            slidesPerView: 5,
                        }
                    }
                });
                console.log("Course slider initialized");
            }
        });
    }

    // Initialize on load and on DOM mutations (for AEM Edit mode)
    document.addEventListener('DOMContentLoaded', initCourseSlider);
    
    const observer = new MutationObserver(initCourseSlider);
    observer.observe(document.body, { childList: true, subtree: true });
})();