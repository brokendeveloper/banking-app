"use client";

import { useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { Button } from "@/components/ui/button";
import { CardDisplay } from "./card-display";
import type { CardResponse } from "@/lib/api/cards";

interface CardCarouselProps {
  cards: CardResponse[];
}

export function CardCarousel({ cards }: CardCarouselProps) {
  const [index, setIndex] = useState(0);
  const [direction, setDirection] = useState(0);

  const prev = () => {
    setDirection(-1);
    setIndex((i) => (i - 1 + cards.length) % cards.length);
  };

  const next = () => {
    setDirection(1);
    setIndex((i) => (i + 1) % cards.length);
  };

  if (!cards.length) return null;

  return (
    <div className="space-y-4">
      <div className="relative overflow-hidden rounded-2xl">
        <AnimatePresence initial={false} custom={direction} mode="wait">
          <motion.div
            key={index}
            custom={direction}
            initial={{ x: direction * 100 + "%", opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: direction * -100 + "%", opacity: 0 }}
            transition={{ type: "spring", stiffness: 300, damping: 30 }}
          >
            <CardDisplay card={cards[index]} />
          </motion.div>
        </AnimatePresence>
      </div>

      {cards.length > 1 && (
        <div className="flex items-center justify-center gap-4">
          <Button variant="ghost" size="icon" onClick={prev}>
            <ChevronLeft className="size-5" />
          </Button>

          <div className="flex gap-1.5">
            {cards.map((_, i) => (
              <button
                key={i}
                onClick={() => { setDirection(i > index ? 1 : -1); setIndex(i); }}
                className={`h-1.5 rounded-full transition-all ${
                  i === index ? "w-5 bg-primary" : "w-1.5 bg-muted-foreground/30"
                }`}
              />
            ))}
          </div>

          <Button variant="ghost" size="icon" onClick={next}>
            <ChevronRight className="size-5" />
          </Button>
        </div>
      )}
    </div>
  );
}
