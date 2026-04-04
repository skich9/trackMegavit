# Design System Specification: Enterprise Modern

## 1. Creative North Star: "The Precision Architect"
This design system rejects the "boxed-in" aesthetic of legacy enterprise software. Instead, it adopts the philosophy of **Precision Architecture**—a digital environment that feels like a high-end physical workspace. We achieve "Enterprise Modern" not through complexity, but through intentionality. 

The system moves beyond the standard grid by utilizing **Tonal Depth** and **Editorial Spacing**. We treat the UI as a series of curated layers where information is prioritized by visual weight and background shifts rather than restrictive lines. Whether in a high-stakes Banking environment or a sterile Laboratory setting, the interface remains a reliable, silent partner for the field professional.

---

## 2. Color & Surface Philosophy

### The "No-Line" Rule
To achieve a signature premium feel, **1px solid borders are strictly prohibited for sectioning.** Boundaries must be defined solely through background shifts. This creates a more organic, fluid interface that reduces cognitive load.
- **Surface Nesting:** A `surface-container-lowest` card should sit atop a `surface-container-low` section. The contrast is subtle, mimicking the way light hits different planes of a physical object.

### Theme Adaptation
The design system supports two distinct tonal modes, driven by the same semantic token structure:

| Token | Banking (Authority & Trust) | Laboratory (Precision & Clarity) |
| :--- | :--- | :--- |
| `primary` | #000f22 (Deep Navy) | #2C7A4D (Emerald) |
| `primary_container` | #0A2540 (The Anchor) | #E8F5E9 (Soft Mint) |
| `tertiary` | #735c00 (Signature Gold) | #49607E (Steel Blue) |
| `surface` | #F7FAFC (Slate Tint) | #FFFFFF (Pure White) |

### Signature Textures
*   **The Glass Layer:** For floating elements (modals, dropdowns, or mobile navigation), use a `surface` color at 85% opacity with a `24px` backdrop blur.
*   **The Power Gradient:** Primary CTAs and Hero KPIs should use a subtle linear gradient (Top-Left to Bottom-Right) transitioning from `primary` to `primary_container`. This adds a "soul" to the data that flat hex codes lack.

---

## 3. Typography: The Editorial Scale
We utilize a dual-typeface system to balance authority with readability.

*   **Display & Headlines (Manrope):** Chosen for its geometric precision and wide apertures. It feels modern and architectural. Use `headline-lg` for dashboard summaries to command attention.
*   **Body & Labels (Inter):** A workhorse for legibility. Its tall x-height ensures that complex data strings (like tracking IDs) are readable even on low-resolution mobile field devices.

**Hierarchy Note:** Always pair a `label-sm` in `on_surface_variant` (all caps, 0.05em tracking) with a `title-lg` value. This "Editorial Pairing" creates an immediate sense of professional hierarchy.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are often messy. This system uses **Ambient Light** principles.

*   **Layering Principle:** 
    1. Base: `surface`
    2. Content Sections: `surface-container-low`
    3. Interaction Cards: `surface-container-lowest`
*   **Ambient Shadows:** For "Floating" states (active cards or mobile FABs), use a multi-layered shadow: `0px 4px 20px rgba(0, 15, 34, 0.06)`. The shadow must be tinted with the `primary` hue to feel integrated into the environment.
*   **The Ghost Border:** If high-contrast accessibility is required, use `outline-variant` at 15% opacity. Never use 100% opaque lines.

---

## 5. Component Patterns

### Dashboard KPI Cards
*   **Structure:** No borders. Use `surface-container-highest` for the background.
*   **Metric:** Use `display-sm` for the primary number.
*   **Trend:** Use `tertiary` (Gold) in Banking or `primary` (Emerald) in Lab for positive trends, placed in a `tertiary-container` chip with `rounded-full` corners.

### Data Tables (The "Fluid" Table)
*   **Header:** `surface-container-low` with `label-md` text.
*   **Rows:** Remove all horizontal rules. Distinguish rows by a `4px` vertical spacing scale or a very subtle `:hover` shift to `surface-container-high`.
*   **Mobile Field Use:** Tables must transform into "Stacked Cards" using `surface-container-lowest` on mobile viewports.

### Form Elements & Inputs
*   **Field Style:** Use a "Soft Inset" look. Background set to `surface-container-highest`, with no border.
*   **Focus State:** A `2px` signature glow using `surface-tint` at 30% opacity.
*   **Collapsible Sidebars:** When collapsed, the sidebar should use `primary_container` with a `glassmorphism` effect, allowing the background data to softly bleed through.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical white space. Let a dashboard card breathe more on the right side than the left to lead the eye.
*   **Do** use `surface-container` tiers to create a "nested" UI.
*   **Do** rely on `tertiary_container` for secondary call-outs—it adds a "premium" accent without competing with the primary action.

### Don't:
*   **Don't** use black (#000000) for text. Use `on_surface` or `on_surface_variant` for a softer, more high-end contrast.
*   **Don't** use 1px solid dividers. If you feel the need for a line, try adding `16px` of additional whitespace or a background color shift instead.
*   **Don't** use standard "Material Blue" for success states. Use the theme’s `primary` (Emerald) or `tertiary` (Gold) to maintain the signature brand identity.