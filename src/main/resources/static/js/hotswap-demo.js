/**
 * This JavaScript file demonstrates hot swapping with static resources.
 * Try changing the code while the application is running.
 * After saving, the browser should automatically refresh to show your changes.
 */

document.addEventListener('DOMContentLoaded', function() {
    // Add a timestamp to show when the page was last loaded
    const timestampElement = document.getElementById('timestamp');
    if (timestampElement) {
        const now = new Date();
        timestampElement.textContent = now.toLocaleString();
    }

    // Add click handler to the demo button
    const demoButton = document.getElementById('demo-button');
    if (demoButton) {
        demoButton.addEventListener('click', function() {
            // Try changing this message while the app is running
            alert('Hello from hot-swapped JavaScript!');
            
            // Change the button text after click
            this.textContent = 'Button Clicked!';
            this.classList.add('clicked');
        });
    }
});
