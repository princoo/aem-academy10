
globalThis.WKND = globalThis.WKND || {};
globalThis.WKND.SliderUtils = globalThis.WKND.SliderUtils || {};

function getSwiperInstance(editable) {
    
    if (!editable?.dom) {
        console.warn("AEM did not provide a DOM reference for this componentf");
        return null;
    }

    const componentDom = editable.dom[0];
    
    const swiperContainer = componentDom.querySelector('.swiper');
    
    if (swiperContainer?.swiper) {
        return swiperContainer.swiper;
    } else {
        console.warn("could not find an active Swiper instance on this component.");
        return null;
    }
}

globalThis.WKND.SliderUtils.slidePrev = function(editable) {
    const swiper = getSwiperInstance(editable);
    if (swiper) {
        swiper.slidePrev();
    }
};

globalThis.WKND.SliderUtils.slideNext = function(editable) {
    const swiper = getSwiperInstance(editable);
    if (swiper) {
        swiper.slideNext();
    }
};