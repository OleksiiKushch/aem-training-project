let cardContainers = document.querySelectorAll('.flip-card-container');

cardContainers.forEach(function(container) {
    container.addEventListener('click', function () {
        this.querySelector('.flip-card').classList.toggle('is-flipped');
    });
});