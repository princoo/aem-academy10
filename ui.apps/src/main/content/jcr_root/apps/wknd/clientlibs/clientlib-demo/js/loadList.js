

const loadMoreBtn = document.getElementById("btn-load-more");
const listContainer = document.getElementById("custom-card-grid");

if (loadMoreBtn && listContainer) {

    loadMoreBtn.addEventListener("click", function () {
        const componentPath = loadMoreBtn.getAttribute("data-component-path");
        const nextPage = loadMoreBtn.getAttribute("data-next-page");

        const url = componentPath + ".loadmore." + nextPage + ".json";

        const originalText = loadMoreBtn.innerText;
        loadMoreBtn.innerText = "Loading...";
        loadMoreBtn.disabled = true;

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error("network response was not ok");
                return response.json();
            })
            .then(data => {
                data.items.forEach(item => {
                    const card = document.createElement("div");
                    card.classList.add("custom-card");

                    const cardContent = document.createElement("div");
                    cardContent.classList.add("card-content");

                    const title = document.createElement("h3");
                    title.classList.add("card-title");
                    title.innerText = item.title;
                    cardContent.appendChild(title);

                    if (item.lastModified) {
                        const date = document.createElement("p");
                        date.classList.add("card-date");
                        date.innerText = item.lastModified;
                        cardContent.appendChild(date);
                    }

                    const desc = document.createElement("p");
                    desc.classList.add("card-description");
                    desc.innerText = item.description || "Read more to explore this topic.";
                    cardContent.appendChild(desc);

                    const cardFooter = document.createElement("div");
                    cardFooter.classList.add("card-footer");

                    const link = document.createElement("a");
                    link.href = item.url;
                    link.classList.add("card-button");
                    link.innerText = "Explore";

                    cardFooter.appendChild(link);

                    card.appendChild(cardContent);
                    card.appendChild(cardFooter);

                    listContainer.appendChild(card);
                });

                if (data.hasNext) {
                    loadMoreBtn.setAttribute("data-next-page", data.nextPage);
                    loadMoreBtn.innerText = originalText;
                    loadMoreBtn.disabled = false;
                } else {
                    loadMoreBtn.style.display = "none";
                }
            })
            .catch(error => {
                loadMoreBtn.innerText = "Error - Try Again";
                loadMoreBtn.disabled = false;
            });
    });
}