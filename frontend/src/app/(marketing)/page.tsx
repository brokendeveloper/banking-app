import { HeroSection } from "@/features/marketing/components/hero-section";
import { StatsSection } from "@/features/marketing/components/stats-section";
import { FeaturesSection } from "@/features/marketing/components/features-section";
import { HowItWorksSection } from "@/features/marketing/components/how-it-works-section";
import { CtaSection } from "@/features/marketing/components/cta-section";

export default function LandingPage() {
  return (
    <div className="flex flex-col">
      <HeroSection />
      <StatsSection />
      <FeaturesSection />
      <HowItWorksSection />
      <CtaSection />
    </div>
  );
}
