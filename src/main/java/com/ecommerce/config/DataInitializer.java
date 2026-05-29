package com.ecommerce.config;

import com.ecommerce.model.Admin;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.AdminRepository;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        seedAdminAccount();
        migrateExistingCustomers();
        seedSampleProducts();
    }

    // Customers created before email-verification was added have emailVerified=false.
    // Auto-verify them so they are not locked out after the upgrade.
    private void migrateExistingCustomers() {
        List<Customer> unverified = customerRepository.findAll().stream()
                .filter(c -> !c.isEmailVerified() && c.getVerificationToken() == null)
                .toList();
        if (!unverified.isEmpty()) {
            unverified.forEach(c -> c.setEmailVerified(true));
            customerRepository.saveAll(unverified);
            System.out.println("Migrated " + unverified.size() + " existing customer(s) to verified status.");
        }
    }

    private void seedAdminAccount() {
        if (adminRepository.findByEmail("admin@shop.com").isEmpty()) {
            Admin admin = new Admin("Shop Admin", "admin@shop.com", "admin123");
            adminRepository.save(admin);
            System.out.println("Default admin created: admin@shop.com / admin123");
        }
    }

    private void add(ProductRepository repo, String name, String desc, double price, int stock, String img, String cat) {
        repo.save(new Product(name, desc, price, stock, img, cat));
    }

    private void seedSampleProducts() {
        if (productRepository.count() > 0) return;

        // ── Electronics ──────────────────────────────────────────────
        add(productRepository, "Wireless Noise-Cancelling Headphones", "Premium over-ear headphones with ANC and 30-hour battery.", 79.99, 50, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&q=80", "Electronics");
        add(productRepository, "Mechanical Keyboard RGB", "Compact TKL keyboard with tactile switches and per-key RGB.", 129.99, 30, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&q=80", "Electronics");
        add(productRepository, "Bluetooth Speaker", "Portable waterproof speaker with 360° sound and 12h battery.", 59.99, 45, "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400&q=80", "Electronics");
        add(productRepository, "Smart Watch", "Fitness tracker with heart rate monitor, GPS and 7-day battery.", 149.99, 60, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&q=80", "Electronics");
        add(productRepository, "Wireless Mouse", "Ergonomic wireless mouse with silent clicks and 18-month battery.", 39.99, 90, "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&q=80", "Electronics");
        add(productRepository, "USB-C Hub 7-in-1", "Multi-port hub with HDMI 4K, 3×USB-A, SD reader and 100W PD.", 49.99, 70, "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=400&q=80", "Electronics");
        add(productRepository, "Portable Charger 20000mAh", "Fast-charge power bank with dual USB-A and USB-C output.", 44.99, 80, "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400&q=80", "Electronics");
        add(productRepository, "Wireless Earbuds", "True wireless earbuds with active noise cancellation and 24h case.", 89.99, 55, "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&q=80", "Electronics");
        add(productRepository, "4K Webcam", "Ultra HD webcam with auto-focus, noise-cancelling mic and privacy shutter.", 119.99, 35, "https://images.unsplash.com/photo-1623949556303-b0d17d198b45?w=400&q=80", "Electronics");
        add(productRepository, "Gaming Mouse", "High-precision gaming mouse, 16000 DPI, 7 programmable buttons.", 59.99, 40, "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=400&q=80", "Electronics");
        add(productRepository, "Monitor LED 27\"", "27-inch QHD IPS monitor, 144Hz refresh rate, 1ms response time.", 299.99, 20, "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&q=80", "Electronics");
        add(productRepository, "Desk Lamp LED", "Adjustable brightness LED lamp with USB charging port and touch control.", 34.99, 60, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400&q=80", "Electronics");
        add(productRepository, "Smart Plug Wi-Fi", "Voice-controlled smart plug compatible with Alexa and Google Home.", 19.99, 120, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&q=80", "Electronics");
        add(productRepository, "Tablet Stand Adjustable", "Aluminum tablet and laptop stand with adjustable angle, 360° rotation.", 29.99, 75, "https://images.unsplash.com/photo-1561154464-82e9adf32764?w=400&q=80", "Electronics");
        add(productRepository, "Ring Light 18\"", "Professional 18-inch ring light with tripod, phone holder and remote.", 69.99, 40, "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&q=80", "Electronics");

        // ── Clothing ─────────────────────────────────────────────────
        add(productRepository, "Classic White T-Shirt", "100% premium cotton crew-neck tee, machine washable, true to size.", 19.99, 200, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&q=80", "Clothing");
        add(productRepository, "Slim Fit Jeans", "Stretch denim slim-fit jeans with 5-pocket design, dark indigo wash.", 49.99, 80, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&q=80", "Clothing");
        add(productRepository, "Hooded Sweatshirt", "Heavyweight 400gsm fleece hoodie with kangaroo pocket and ribbed cuffs.", 59.99, 100, "https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=400&q=80", "Clothing");
        add(productRepository, "Chino Pants", "Slim-fit chino trousers in stretch cotton, wrinkle-resistant finish.", 54.99, 70, "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=400&q=80", "Clothing");
        add(productRepository, "Polo Shirt", "Breathable pique cotton polo with embroidered logo, casual or smart.", 34.99, 110, "https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=400&q=80", "Clothing");
        add(productRepository, "Denim Jacket", "Classic trucker denim jacket with button closure and chest pockets.", 79.99, 45, "https://images.unsplash.com/photo-1551537482-f2075a1d41f2?w=400&q=80", "Clothing");
        add(productRepository, "Graphic Print T-Shirt", "Unisex graphic tee with artistic print, soft ring-spun cotton fabric.", 24.99, 150, "https://images.unsplash.com/photo-1527719327859-c952e6e89cff?w=400&q=80", "Clothing");
        add(productRepository, "Fleece Jacket", "Lightweight anti-pilling fleece jacket with full-zip and side pockets.", 69.99, 60, "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=400&q=80", "Clothing");
        add(productRepository, "Athletic Shorts", "4-inch inseam performance shorts with mesh liner and zip pocket.", 29.99, 130, "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=400&q=80", "Clothing");
        add(productRepository, "Dress Shirt Button-Down", "Formal Oxford cotton dress shirt, non-iron, slim fit collar.", 44.99, 85, "https://images.unsplash.com/photo-1620012253295-c15cc3e65df4?w=400&q=80", "Clothing");
        add(productRepository, "Cargo Pants", "Multi-pocket cargo trousers with adjustable waist and ripstop fabric.", 64.99, 55, "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400&q=80", "Clothing");
        add(productRepository, "Windbreaker Jacket", "Packable windbreaker with DWR coating, hidden hood and chest pocket.", 89.99, 40, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400&q=80", "Clothing");
        add(productRepository, "Knit Sweater", "Merino wool blend knit pullover with ribbed hem and crew neck.", 74.99, 65, "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=400&q=80", "Clothing");

        // ── Footwear ─────────────────────────────────────────────────
        add(productRepository, "Running Shoes", "Lightweight and breathable with responsive foam cushioning for daily runs.", 89.99, 75, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&q=80", "Footwear");
        add(productRepository, "Casual Sneakers", "Clean everyday leather sneakers with padded insole and rubber sole.", 69.99, 80, "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&q=80", "Footwear");
        add(productRepository, "Hiking Boots", "Waterproof mid-cut hiking boots with vibram outsole and ankle support.", 129.99, 40, "https://images.unsplash.com/photo-1520219306100-ec4afeeefe58?w=400&q=80", "Footwear");
        add(productRepository, "Slip-On Loafers", "Comfortable canvas slip-on shoes with elastic gussets and memory foam insole.", 44.99, 90, "https://images.unsplash.com/photo-1603487742131-4160ec999306?w=400&q=80", "Footwear");
        add(productRepository, "High-Top Basketball Shoes", "Performance court shoes with ankle strap, cushioned midsole and grip outsole.", 109.99, 35, "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=400&q=80", "Footwear");
        add(productRepository, "Leather Oxford Shoes", "Classic formal Oxford in genuine leather with Blake-stitched construction.", 119.99, 30, "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400&q=80", "Footwear");
        add(productRepository, "Sports Sandals", "Adjustable strap sandals with arch support and EVA footbed for outdoor use.", 39.99, 70, "https://images.unsplash.com/photo-1603487742131-4160ec999306?w=400&q=80", "Footwear");
        add(productRepository, "Chelsea Boots", "Elastic-sided Chelsea boots in suede leather with stacked heel.", 99.99, 45, "https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400&q=80", "Footwear");

        // ── Accessories ──────────────────────────────────────────────
        add(productRepository, "Laptop Backpack 30L", "Padded laptop compartment, USB charging port and anti-theft zipper.", 49.99, 100, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&q=80", "Accessories");
        add(productRepository, "Leather Wallet Slim", "Minimalist bifold wallet in genuine leather, holds 8 cards and cash.", 29.99, 140, "https://images.unsplash.com/photo-1624913503273-5f9c4e980dba?w=400&q=80", "Accessories");
        add(productRepository, "Sunglasses Polarized", "UV400 polarized sunglasses with TR90 frame and spring hinges.", 34.99, 95, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400&q=80", "Accessories");
        add(productRepository, "Baseball Cap", "Adjustable structured cap with embroidered logo and breathable mesh back.", 24.99, 160, "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400&q=80", "Accessories");
        add(productRepository, "Leather Belt", "Genuine full-grain leather belt with brushed nickel buckle, 35mm width.", 34.99, 110, "https://images.unsplash.com/photo-1624623278313-a930126a11c3?w=400&q=80", "Accessories");
        add(productRepository, "Wool Scarf", "Soft merino wool scarf, 180cm length, available in classic colours.", 39.99, 75, "https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=400&q=80", "Accessories");
        add(productRepository, "Canvas Tote Bag", "Heavy-duty 12oz canvas tote with inside zip pocket and laptop sleeve.", 27.99, 120, "https://images.unsplash.com/photo-1572751016484-a2b94d95f5f0?w=400&q=80", "Accessories");
        add(productRepository, "Watch Band Leather", "Genuine leather replacement band, 20mm, compatible with most watches.", 19.99, 200, "https://images.unsplash.com/photo-1517142089942-ba376ce32a2e?w=400&q=80", "Accessories");
        add(productRepository, "Crossbody Bag", "Compact crossbody with adjustable strap, organiser pockets and YKK zips.", 54.99, 65, "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=400&q=80", "Accessories");
        add(productRepository, "Knit Beanie Hat", "Chunky ribbed beanie in 100% acrylic, folds up for extra warmth.", 17.99, 180, "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=400&q=80", "Accessories");

        // ── Home & Kitchen ────────────────────────────────────────────
        add(productRepository, "Stainless Water Bottle 500ml", "Double-walled insulated bottle, keeps cold 24h or hot 12h, BPA-free.", 24.99, 120, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&q=80", "Home");
        add(productRepository, "Pour-Over Coffee Set", "Borosilicate glass dripper with stainless reusable filter and carafe.", 44.99, 50, "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&q=80", "Home");
        add(productRepository, "Non-Stick Frying Pan 28cm", "Granite-coated non-stick pan with heat-resistant silicone handle.", 34.99, 80, "https://images.unsplash.com/photo-1585515320310-259814833e62?w=400&q=80", "Home");
        add(productRepository, "Scented Soy Candle Set", "Set of 3 hand-poured soy candles in glass jars — lavender, vanilla, cedar.", 32.99, 90, "https://images.unsplash.com/photo-1602607144840-32e37915d375?w=400&q=80", "Home");
        add(productRepository, "Bamboo Cutting Board", "Extra-large bamboo board with juice groove and non-slip rubber feet.", 29.99, 100, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&q=80", "Home");
        add(productRepository, "Throw Blanket Knit", "Super-soft chunky knit throw, 130×180cm, washable, multiple colours.", 49.99, 70, "https://images.unsplash.com/photo-1580301763395-8e0c9b6a0d5d?w=400&q=80", "Home");
        add(productRepository, "Ceramic Mug Set of 4", "Handmade ceramic mugs 350ml, microwave and dishwasher safe.", 38.99, 85, "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400&q=80", "Home");
        add(productRepository, "Digital Kitchen Scale", "Precision scale 0.1g/5kg range, removable bowl and tare function.", 22.99, 110, "https://images.unsplash.com/photo-1591017403286-fd8493524e1e?w=400&q=80", "Home");
        add(productRepository, "Linen Cushion Covers Set", "Set of 4 linen-blend cushion covers with hidden zip, 45×45cm.", 27.99, 130, "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&q=80", "Home");
        add(productRepository, "Wall Clock Minimalist", "Silent sweep wall clock, 30cm diameter, wooden frame, no ticking.", 29.99, 60, "https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?w=400&q=80", "Home");
        add(productRepository, "Indoor Plant Pot Set", "Set of 3 ceramic pots with drainage holes and bamboo saucers.", 34.99, 75, "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=400&q=80", "Home");
        add(productRepository, "French Press 1L", "Double-wall stainless steel French press for rich full-bodied coffee.", 39.99, 55, "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=400&q=80", "Home");
        add(productRepository, "Dish Rack Stainless", "Rust-proof stainless dish rack with cutlery holder and drip tray.", 42.99, 65, "https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=400&q=80", "Home");

        // ── Sports & Fitness ──────────────────────────────────────────
        add(productRepository, "Yoga Mat 6mm", "Eco-friendly TPE yoga mat with alignment lines, non-slip surface.", 35.99, 90, "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&q=80", "Sports");
        add(productRepository, "Resistance Bands Set", "Set of 5 fabric resistance bands (10–50 lbs) with carry bag.", 24.99, 120, "https://images.unsplash.com/photo-1598632640487-6ea4a4e8b963?w=400&q=80", "Sports");
        add(productRepository, "Adjustable Dumbbells 20kg", "Dial-select adjustable dumbbell pair, 2–20kg per handle, compact.", 189.99, 15, "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=400&q=80", "Sports");
        add(productRepository, "Jump Rope Speed", "Ball-bearing speed rope with steel cable and soft foam handles.", 14.99, 200, "https://images.unsplash.com/photo-1598928636135-d146006ff4be?w=400&q=80", "Sports");
        add(productRepository, "Foam Roller 33cm", "High-density EVA foam roller for muscle recovery and myofascial release.", 22.99, 100, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&q=80", "Sports");
        add(productRepository, "Water Bottle Sport 1L", "BPA-free Tritan bottle with flip lid and carry loop, leakproof.", 18.99, 160, "https://images.unsplash.com/photo-1523362628745-0c100150b504?w=400&q=80", "Sports");
        add(productRepository, "Running Armband", "Sweat-proof phone armband with touch screen and key pocket.", 12.99, 180, "https://images.unsplash.com/photo-1553440569-bcc63803a83d?w=400&q=80", "Sports");
        add(productRepository, "Gym Bag 40L", "Wet/dry compartment gym bag with shoe pocket and trolley strap.", 44.99, 70, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&q=80", "Sports");
        add(productRepository, "Kettlebell 16kg", "Cast iron competition kettlebell with flat base and smooth handle.", 54.99, 30, "https://images.unsplash.com/photo-1581009137042-c552e485697a?w=400&q=80", "Sports");
        add(productRepository, "Cycling Gloves", "Half-finger padded cycling gloves with anti-slip silicone grip.", 19.99, 110, "https://images.unsplash.com/photo-1571731956672-f2b94d7dd0cb?w=400&q=80", "Sports");

        // ── Books & Stationery ────────────────────────────────────────
        add(productRepository, "Hardcover Dotted Journal A5", "192-page hardcover bullet journal with dotted grid, lay-flat binding.", 18.99, 150, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&q=80", "Books");
        add(productRepository, "Fountain Pen Set", "Stainless steel nib fountain pen with 5 ink cartridges, matte black.", 29.99, 80, "https://images.unsplash.com/photo-1562157873-818bc0726f68?w=400&q=80", "Books");
        add(productRepository, "Sticky Notes Assorted Pack", "Pack of 8 pads in pastel colours, 75×75mm, repositionable adhesive.", 9.99, 300, "https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400&q=80", "Books");
        add(productRepository, "Desk Organiser Bamboo", "6-compartment bamboo desk organiser with pen holder and tray.", 24.99, 95, "https://images.unsplash.com/photo-1589578228447-e1a4e481c6c8?w=400&q=80", "Books");
        add(productRepository, "Mechanical Pencil Set", "Set of 3 precision mechanical pencils (0.3/0.5/0.7mm) with lead refills.", 16.99, 120, "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=400&q=80", "Books");
        add(productRepository, "Wireless Charging Pad", "10W fast wireless charger compatible with Qi devices, LED indicator.", 22.99, 130, "https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?w=400&q=80", "Electronics");
        add(productRepository, "Cable Management Box", "Wooden cable tidy box with 3 cable exit slots, fits power strips.", 27.99, 85, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&q=80", "Home");

        // ── Beauty & Personal Care ────────────────────────────────────
        add(productRepository, "Electric Face Brush", "Silicone sonic face cleansing brush with 3 speeds, waterproof.", 39.99, 70, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&q=80", "Beauty");
        add(productRepository, "Jade Face Roller", "100% natural jade stone roller for reducing puffiness and improving circulation.", 17.99, 140, "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=400&q=80", "Beauty");
        add(productRepository, "Aromatherapy Diffuser 400ml", "Ultrasonic essential oil diffuser with 7-colour LED and timer function.", 34.99, 65, "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=400&q=80", "Beauty");
        add(productRepository, "Hair Dryer 2200W", "Professional ionic hair dryer with 3 heat settings and cool shot button.", 49.99, 55, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400&q=80", "Beauty");
        add(productRepository, "Nail Care Kit 7-piece", "Stainless steel manicure set with cuticle pusher, file and clippers.", 19.99, 110, "https://images.unsplash.com/photo-1604654894610-df63bc536371?w=400&q=80", "Beauty");

        // ── Outdoor & Travel ──────────────────────────────────────────
        add(productRepository, "Travel Packing Cubes Set 6", "Lightweight mesh packing cubes in S/M/L/XL, zipped with handles.", 27.99, 100, "https://images.unsplash.com/photo-1553531384-397c80973a0b?w=400&q=80", "Travel");
        add(productRepository, "Neck Pillow Memory Foam", "Ergonomic travel pillow with machine-washable cover and carry loop.", 29.99, 90, "https://images.unsplash.com/photo-1520466809213-7b9a56adcd45?w=400&q=80", "Travel");
        add(productRepository, "Portable Luggage Scale", "Handheld digital scale up to 50kg with backlit display and tare function.", 14.99, 150, "https://images.unsplash.com/photo-1461354464878-ad92f492a5a0?w=400&q=80", "Travel");
        add(productRepository, "Waterproof Rain Poncho", "Lightweight packable rain poncho with hood, one size fits all.", 12.99, 200, "https://images.unsplash.com/photo-1530259152377-3a014e1092d6?w=400&q=80", "Travel");
        add(productRepository, "Universal Travel Adapter", "All-in-one travel plug adapter for 150+ countries with 4 USB ports.", 32.99, 80, "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=400&q=80", "Travel");
        add(productRepository, "Insulated Lunch Bag", "Thermal lunch bag with 3 compartments, leak-proof and easy-clean lining.", 22.99, 120, "https://images.unsplash.com/photo-1578496480240-32d3e0c04525?w=400&q=80", "Travel");

        System.out.println("Sample products loaded: " + productRepository.count() + " products.");
    }
}
